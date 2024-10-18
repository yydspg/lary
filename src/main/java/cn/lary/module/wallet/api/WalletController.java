package cn.lary.module.wallet.api;

import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.wallet.core.WalletBizExecute;
import cn.lary.module.wallet.dto.RechargeDTO;
import cn.lary.module.wallet.dto.UpdateSecurityQuestionDTO;
import cn.lary.module.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.module.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.module.wallet.vo.BalanceVO;
import cn.lary.module.wallet.vo.WalletIncomeVO;
import cn.lary.module.wallet.vo.WalletOutcomeVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        ResponsePair<PayBuildVO> pair= walletBizExecute.recharge(req);
        if (pair.isFail()) {
            return pair.getMsg();
        }
        response.setContentType("text/html;charset=utf-8");
        PayBuildVO data =  pair.getData();
        return data.getBody();
    }

    /**
     * 获取账户钱包信息
     * @return {@link BalanceVO}
     */
    @PostMapping("/balance")
    public SingleResponse<BalanceVO> getBalance() {

        ResponsePair<BalanceVO> response = walletBizExecute.getBalance();
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 更新 密保 question
     * @param req {@link UpdateSecurityQuestionDTO}
     * @return ok
     */
    @PostMapping("/question")
    public SingleResponse<Void> updatePwd(@RequestBody @Valid UpdateSecurityQuestionDTO req) {
        ResponsePair<Void> response = walletBizExecute.updateQuestion(req);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 获取收入
     * @param req {@link WalletIncomePageQueryDTO}
     * @return {@link WalletIncomeVO}
     */
    @PostMapping("/incomes")
    public PageResponse<WalletIncomeVO> incomes(@RequestBody @Valid WalletIncomePageQueryDTO req) {
        ResponsePair<List<WalletIncomeVO>> response = walletBizExecute.getIncomeVOs(req);
        if (response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),req.getPageIndex(),req.getPageSize());
    }

    /**
     * 获取支出
     * @param req {@link WalletOutcomePageQueryDTO}
     * @return {@link WalletOutcomeVO}
     */
    @PostMapping("/outcomes")
    public PageResponse<WalletOutcomeVO> outcomes(@RequestBody @Valid WalletOutcomePageQueryDTO req) {
        ResponsePair<List<WalletOutcomeVO>> response = walletBizExecute.getOutcomeVOs(req);
        if (response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),req.getPageIndex(),req.getPageSize());
    }
}
