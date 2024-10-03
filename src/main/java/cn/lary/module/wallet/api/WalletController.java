package cn.lary.module.wallet.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.BizKit;
import cn.lary.kit.ResKit;
import cn.lary.module.gift.dto.UpdateSecurityQuestionDTO;
import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.user.service.UserService;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.wallet.core.WalletBizExecute;
import cn.lary.module.wallet.dto.RechargeDTO;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.vo.BalanceVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  钱包模块
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

    /**
     * 钱包充值
     * @param req {@link RechargeDTO}
     * @return form/html
     */
    @PostMapping("/recharge")
    @ResponseBody
    public String recharge(@Valid @RequestBody RechargeDTO req, HttpServletResponse response) {
        int uid = ReqContext.getLoginUID();
        ResPair<PayBuildVO> res = walletBizExecute.recharge(req, uid);
        if (!res.isOk()) {
            return res.getMsg();
        }
        response.setContentType("text/html;charset=utf-8");
        PayBuildVO data =  res.getData();
        return data.getBody();
    }

    /**
     * 获取账户钱包信息
     * @return {@link BalanceVO}
     */
    @PostMapping("/balance")
    public SingleResponse<BalanceVO> getBalance() {
        int uid = ReqContext.getLoginUID();
        ResPair<BalanceVO> res = walletBizExecute.getBalance(uid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }

    /**
     * 更新 密保 question
     * @param req {@link UpdateSecurityQuestionDTO}
     * @return ok
     */
    @PostMapping("/question")
    public SingleResponse<Void> updatePwd(@RequestBody @Valid UpdateSecurityQuestionDTO req) {
        int uid = ReqContext.getLoginUID();
        ResPair<Void> res = walletBizExecute.updateQuestion(uid, req);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }

}
