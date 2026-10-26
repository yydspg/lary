// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.data.Row;
import io.lark.column.tensor.Tensor;
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

import java.util.List;

/**
 * Search/retargeting tensor demo.
 *
 * <p>Nine business modules compute CTR, CVR, CTCVR, same-item rebuy loss,
 * buy-again share, coupon ROI, retargeting metrics, GMV and delay. Retargeting
 * is a 2-D tensor and delay is a 3-D tensor; all other outputs are normal 1-D
 * columns.
 *
 * <p>Run with {@code ./gradlew :examples:runSearch}.
 */
public final class SearchDemo {

    private SearchDemo() {
    }

    public static void main(String[] args) {
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

        try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
            pipeline.addAll(inputs, ctr, cvr, ctcvr, gmv, coupon,
                            buyAgain, rebuy, retarget, delay)
                    .compile()
                    .feed(SearchFeed.sample(inputs))
                    .compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    ctr.ctr(), cvr.cvr(), ctcvr.ctcvr(),
                    buyAgain.buyagain(), rebuy.rebuySameLoss(),
                    coupon.couponRoi(), gmv.gmv()));

            System.out.println("=== search / retargeting metrics ===");
            SearchResultPrinter.printTable(rows);

            Tensor retargetTensor = pipeline.fetchTensor(retarget.retargetAd());
            System.out.printf("retarget_ad shape=%s size=%d%n",
                    retargetTensor.shape(), retargetTensor.size());
            double[] retargetValues = retargetTensor.doubleData();
            for (int i = 0; i < retargetTensor.shape().dim(0); i++) {
                System.out.printf("  row %d: click_share=%.4f cost_share=%.4f roi=%.4f%n",
                        i, retargetValues[i * 3], retargetValues[i * 3 + 1],
                        retargetValues[i * 3 + 2]);
            }

            Tensor delayTensor = pipeline.fetchTensor(delay.delay());
            System.out.printf("delay shape=%s size=%d%n",
                    delayTensor.shape(), delayTensor.size());
            double[] delayValues = delayTensor.doubleData();
            for (int i = 0; i < delayTensor.shape().dim(0); i++) {
                int base = i * 4;
                System.out.printf("  row %d: order-on-time=%.4f order-delayed=%.4f "
                                + "gmv-on-time=%.4f gmv-delayed=%.4f%n",
                        i, delayValues[base], delayValues[base + 1],
                        delayValues[base + 2], delayValues[base + 3]);
            }

            System.out.print(pipeline.stats().summary());
        }
    }
}
