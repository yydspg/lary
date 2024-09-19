package cn.lary.module.wallet.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.pay.dto.PayBuildDTO;
import cn.lary.module.wallet.core.WalletBizExecute;
import cn.lary.module.wallet.dto.RechargeReq;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.vo.BalanceVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author paul
 * @since 2024-09-13
 */
@RestController
@RequestMapping("/v1/wallet")
@RequiredArgsConstructor
public class WalletController {


    private final WalletBizExecute walletBizExecute;


    @PostMapping("/recharge")
    @ResponseBody
    public String recharge(@Valid @RequestBody RechargeReq req,HttpServletResponse response) {
        String uid = ReqContext.getLoginUID();
        ResPair<PayBuildDTO> res = walletBizExecute.recharge(req, uid);
        if (!res.isOk()) {
            return res.getMsg();
        }
        response.setContentType("text/html;charset=utf-8");
        PayBuildDTO data =  res.getData();
        return data.getBody();
    }

    @PostMapping("/balance")
    public SingleResponse<BalanceVO> getBalance() {
        String uid = ReqContext.getLoginUID();
        ResPair<Wallet> res = walletBizExecute.getBalance(uid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        Wallet data = res.getData();
        BalanceVO vo = new BalanceVO().setVcIncome(data.getVcIncome()).setIsAnchor(data.getIsAnchor())
                .setVcFee(data.getVcFee()).setVcOutcome(data.getVcOutcome()).setVcLocked(data.getVcLocked());
        return ResKit.ok(vo);
    }
}
