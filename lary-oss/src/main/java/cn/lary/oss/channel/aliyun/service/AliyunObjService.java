package cn.lary.oss.channel.aliyun.service;

import cn.lary.oss.channel.aliyun.convertor.args.ArgsToDeleteObjectsRequestConvert;
import cn.lary.oss.channel.aliyun.convertor.args.ArgsToListObjectsRequestConvert;
import cn.lary.oss.channel.aliyun.convertor.args.ArgsToListObjectsV2RequestConvert;
import cn.lary.oss.channel.aliyun.convertor.domain.ListObjectsV2ResultConvert;
import cn.lary.oss.channel.aliyun.convertor.domain.ObjectListeningConvert;
import cn.lary.oss.channel.aliyun.define.pool.AliyunClientPool;
import cn.lary.oss.channel.aliyun.define.service.AliyunBaseService;
import cn.lary.oss.standard.args.obj.*;
import cn.lary.oss.standard.domain.obj.ListObj;
import cn.lary.oss.standard.domain.obj.ListV2Obj;
import cn.lary.oss.standard.domain.obj.Obj;
import cn.lary.oss.standard.service.ObjService;
import cn.lary.oss.util.ConvertKit;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author paul 2024/4/18
 */

@Service
public class AliyunObjService extends AliyunBaseService implements ObjService {


    public AliyunObjService(AliyunClientPool objectPool) {
        super(objectPool);
    }

    @Override
    public ListV2Obj listV2(ListV2ObjArgs args) {
        OSS cli = getCli();
        try {
            ListObjectsV2Result res = cli.listObjectsV2(ConvertKit.convert(args, new ArgsToListObjectsV2RequestConvert()));
            return ConvertKit.convert(res,new ListObjectsV2ResultConvert());
        }finally {
            close(cli);
        }
    }

    @Override
    public ListObj list(ListObjArgs args) {
        OSS cli = getCli();
        try {
            ObjectListing res = cli.listObjects(ConvertKit.convert(args, new ArgsToListObjectsRequestConvert()));
            return ConvertKit.convert(res,new ObjectListeningConvert());
        } finally {
            close(cli);
        }
    }

    @Override
    public Obj create(CreateObjArgs args) {
        OSS cli = getCli();
        try {
            ObjectMetadata data = new ObjectMetadata();
            data.setUserMetadata(args.getMetadata());
            PutObjectResult res = cli.putObject(args.getBucketName(), args.getObjectName(), args.getInputStream(),data);
            Obj obj = new Obj();
            obj.setBucketName(args.getBucketName());
            obj.setObjectName(args.getObjectName());
            obj.setETag(res.getETag());
            obj.setVersionId(res.getVersionId());
            return obj;
        } finally {
            super.close(cli);
        }
    }

    @Override
    public void del(DelObjArgs args) {
        OSS cli = getCli();
        try {
            cli.deleteObject(args.getBucketName(),args.getObjectName());
        }finally {
            super.close(cli);
        }
    }

    @Override
    public void del(DelMultiObjArgs args) {
        OSS cli = getCli();
        try {
            cli.deleteObjects(ConvertKit.convert(args,new ArgsToDeleteObjectsRequestConvert()));
        }finally {
            close(cli);
        }
    }

    @Override
    public Object get(GetObjArgs args) {
        OSS cli = getCli();
        try {
            SelectObjectRequest req = new SelectObjectRequest(args.getBucketName(), args.getObjectName());
            return cli.selectObject(req);
        } finally {
            close(cli);
        }
    }

    @Override
    public void rename(CreateObjArgs args) {
        OSS cli = getCli();
        try {
            cli.copyObject(args.getBucketName(), args.getObjectName(), args.getBucketName(), args.getRename());
            cli.deleteObject(args.getBucketName(),args.getObjectName());
        } finally {
            close(cli);
        }
    }

    @Override
    public boolean exist(GetObjArgs args) {
        OSS cli = getCli();
        try {
            return cli.doesObjectExist(args.getBucketName(), args.getObjectName());
        } finally {
            close(cli);
        }
    }
}
