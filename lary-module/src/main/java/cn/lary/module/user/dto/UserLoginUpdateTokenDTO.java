package cn.lary.module.user.dto;

import cn.lary.external.wk.dto.user.UpdateTokenDTO;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class UserLoginUpdateTokenDTO extends UpdateTokenDTO {

}