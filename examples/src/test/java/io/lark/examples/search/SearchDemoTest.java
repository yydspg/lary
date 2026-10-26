// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.data.Row;
import io.lark.column.data.Tensor;
import io.lark.column.data.TensorShape;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.examples.search.data.SearchFeed;
import io.lark.examples.search.module.BuyAgainModule;
import io.lark.examples.search.module.CouponRoiModule;
import io.lark.examples.search.module.CtCvrModule;
import io.lark.examples.search.module.CtrModule;
import io.lark.examples.search.module.CvrModule;
import io.lark.examples.search.module.DelayModule;
import io.lark.examples.search.module.GmvModule;
import io.lark.examples.search.module.RebuySameLossModule;
import io.lark.examples.search.module.RetargetAdModule;
import io.lark.examples.search.module.SearchInputModule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchDemoTest {

    @Test
    void computesScalarAndShapedSearchMetrics() {
        SearchInputModule inputs = new SearchInputModule();
        CtrModule ctr = new CtrModule();
        CvrModule cvr = new CvrModule();
        CtCvrModule ctcvr = new CtCvrModule(ctr, cvr);
        GmvModule gmv = new GmvModule();
        CouponRoiModule coupon = new CouponRoiModule(gmv);
        BuyAgainModule buyAgain = new BuyAgainModule();
        RebuySameLossModule rebuy = new RebuySameLossModule(buyAgain);
        RetargetAdModule retarget = new RetargetAdModule();
        DelayModule delay = new DelayModule();

        try (ColumnPipeline pipeline = new ColumnPipeline(3)) {
            pipeline.addAll(inputs, ctr, cvr, ctcvr, gmv, coupon,
                            buyAgain, rebuy, retarget, delay)
                    .compile()
                    .feed(SearchFeed.sample(inputs))
                    .compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    ctr.ctr(), cvr.cvr(), ctcvr.ctcvr(),
                    buyAgain.buyagain(), rebuy.rebuySameLoss(),
                    coupon.couponRoi(), gmv.gmv()));

            assertEquals(5, rows.size());
            assertEquals(0.05, rows.get(0).get(0).asDouble(), 1e-9);
            assertEquals(0.10, rows.get(0).get(1).asDouble(), 1e-9);
            assertEquals(0.005, rows.get(0).get(2).asDouble(), 1e-9);
            assertEquals(0.20, rows.get(0).get(3).asDouble(), 1e-9);
            assertEquals(0.80, rows.get(0).get(4).asDouble(), 1e-9);
            assertEquals(50.0, rows.get(0).get(5).asDouble(), 1e-9);
            assertEquals(500.0, rows.get(0).get(6).asDouble(), 1e-9);

            // Zero coupon cost / zero retarget cost must degrade to zero.
            assertEquals(0.0, rows.get(2).get(5).asDouble(), 1e-9);

            Tensor retargetTensor = pipeline.fetchTensor(retarget.retargetAd());
            assertEquals(TensorShape.of(5, 3), retargetTensor.shape());
            assertEquals(0.40, retargetTensor.getDouble(0), 1e-9);
            assertEquals(0.004, retargetTensor.getDouble(1), 1e-9);
            assertEquals(250.0, retargetTensor.getDouble(2), 1e-9);
            assertEquals(0.0, retargetTensor.getDouble(2 * 3 + 2), 1e-9);

            Tensor delayTensor = pipeline.fetchTensor(delay.delay());
            assertEquals(TensorShape.of(5, 2, 2), delayTensor.shape());
            assertEquals(0.80, delayTensor.getDouble(0), 1e-9);
            assertEquals(0.20, delayTensor.getDouble(1), 1e-9);
            assertEquals(0.90, delayTensor.getDouble(2), 1e-9);
            assertEquals(0.10, delayTensor.getDouble(3), 1e-9);
            assertEquals(1.0, delayTensor.getDouble(2 * 4), 1e-9);
        }
    }
}
