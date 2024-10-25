package cn.lary.module.wallet.api;

import cn.lary.common.dto.PageResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.wallet.component.WalletExecute;
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

    private final WalletExecute walletExecute;

    /**
     * 钱包充值
     * @param dto {@link RechargeDTO}
     * @return form/html
     */
    @PostMapping("/recharge")
    @ResponseBody
    public String recharge(@Valid @RequestBody RechargeDTO dto, HttpServletResponse response) {
        ResponsePair<PaymentBuildVO> pair= walletExecute.recharge(dto);
        if (pair.isFail()) {
            return pair.getMsg();
        }
        response.setContentType("text/html;charset=utf-8");
        PaymentBuildVO data =  pair.getData();
        return data.getBody();
    }

    /**
     * 获取账户钱包信息
     * @return {@link BalanceVO}
     */
    @PostMapping("/balance")
    public SingleResponse<BalanceVO> getBalance() {

        ResponsePair<BalanceVO> response = walletExecute.getBalance();
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
        ResponsePair<Void> response = walletExecute.updateQuestion(req);
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
        ResponsePair<List<WalletIncomeVO>> response = walletExecute.getIncomeVOs(req);
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
    public PageResponse<WalletOutcomeVO> outcomes(@RequestBody @Valid WalletOutcomePageQueryDTO dto) {
        ResponsePair<List<WalletOutcomeVO>> response = walletExecute.getOutcomeVOs(dto);
        if (response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),dto.getPageIndex(),dto.getPageSize());
    }
}
