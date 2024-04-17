package cn.lary.oss.standard.args.common.impl;

import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class PutObjBaseArgs extends ObjWArgs{
        protected Long objectSize;
        protected Long partSize;
        protected String contentType;
}
