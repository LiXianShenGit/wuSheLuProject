package cn.iocoder.yudao.module.pay.service.merchant;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.pay.core.client.PayClientFactory;
import cn.iocoder.yudao.framework.pay.core.client.impl.alipay.AlipayPayClientConfig;
import cn.iocoder.yudao.framework.pay.core.client.impl.wx.WXPayClientConfig;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.pay.controller.admin.merchant.vo.channel.PayChannelCreateReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.merchant.vo.channel.PayChannelExportReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.merchant.vo.channel.PayChannelPageReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.merchant.vo.channel.PayChannelUpdateReqVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.merchant.PayChannelDO;
import cn.iocoder.yudao.module.pay.dal.mysql.merchant.PayChannelMapper;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.buildTime;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.pay.enums.ErrorCodeConstants.CHANNEL_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

@Import({PayChannelServiceImpl.class})
public class PayChannelServiceTest extends BaseDbUnitTest {

    @Resource
    private PayChannelServiceImpl channelService;

    @Resource
    private PayChannelMapper channelMapper;

    @MockBean
    private PayClientFactory payClientFactory;
    @MockBean
    private Validator validator;

    @Test
    public void testCreateWechatVersion2Channel_success() {
        // ????????????
        WXPayClientConfig v2Config = getV2Config();
        PayChannelCreateReqVO reqVO = randomPojo(PayChannelCreateReqVO.class, o -> {
            o.setCode(PayChannelEnum.WX_PUB.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setConfig(JSON.toJSONString(v2Config));
        });

        // ??????
        Long channelId = channelService.createChannel(reqVO);
        // ??????
        assertNotNull(channelId);
        // ?????????????????????????????????
        PayChannelDO channel = channelMapper.selectById(channelId);
        assertPojoEquals(reqVO, channel, "config");
        // ??????config ?????????????????????????????????
        assertPojoEquals(v2Config, channel.getConfig());
    }

    @Test
    public void testCreateWechatVersion3Channel_success() {
        // ????????????
        WXPayClientConfig v3Config = getV3Config();
        PayChannelCreateReqVO reqVO = randomPojo(PayChannelCreateReqVO.class, o -> {
            o.setCode(PayChannelEnum.WX_PUB.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setConfig(JSON.toJSONString(v3Config));
        });

        // ??????
        Long channelId = channelService.createChannel(reqVO);
        // ??????
        assertNotNull(channelId);
        // ?????????????????????????????????
        PayChannelDO channel = channelMapper.selectById(channelId);
        assertPojoEquals(reqVO, channel, "config");
        // ??????config ?????????????????????????????????
        assertPojoEquals(v3Config, channel.getConfig());
    }

    @Test
    public void testCreateAliPayPublicKeyChannel_success() {
        // ????????????

        AlipayPayClientConfig payClientConfig = getPublicKeyConfig();
        PayChannelCreateReqVO reqVO = randomPojo(PayChannelCreateReqVO.class, o -> {
            o.setCode(PayChannelEnum.ALIPAY_APP.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setConfig(JSON.toJSONString(payClientConfig));
        });

        // ??????
        Long channelId = channelService.createChannel(reqVO);
        // ??????
        assertNotNull(channelId);
        // ?????????????????????????????????
        PayChannelDO channel = channelMapper.selectById(channelId);
        assertPojoEquals(reqVO, channel, "config");
        // ??????config ?????????????????????????????????
        assertPojoEquals(payClientConfig, channel.getConfig());

    }

    @Test
    public void testCreateAliPayCertificateChannel_success() {
        // ????????????

        AlipayPayClientConfig payClientConfig = getCertificateConfig();
        PayChannelCreateReqVO reqVO = randomPojo(PayChannelCreateReqVO.class, o -> {
            o.setCode(PayChannelEnum.ALIPAY_APP.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setConfig(JSON.toJSONString(payClientConfig));
        });

        // ??????
        Long channelId = channelService.createChannel(reqVO);
        // ??????
        assertNotNull(channelId);
        // ?????????????????????????????????
        PayChannelDO channel = channelMapper.selectById(channelId);
        assertPojoEquals(reqVO, channel, "config");
        // ??????config ?????????????????????????????????
        assertPojoEquals(payClientConfig, channel.getConfig());
    }

    @Test
    public void testUpdateChannel_success() {
        // mock ??????
        AlipayPayClientConfig payClientConfig = getCertificateConfig();
        PayChannelDO dbChannel = randomPojo(PayChannelDO.class, o -> {
            o.setCode(PayChannelEnum.ALIPAY_APP.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setConfig(payClientConfig);
        });
        channelMapper.insert(dbChannel);// @Sql: ?????????????????????????????????
        // ????????????
        AlipayPayClientConfig payClientPublicKeyConfig = getPublicKeyConfig();
        PayChannelUpdateReqVO reqVO = randomPojo(PayChannelUpdateReqVO.class, o -> {
            o.setCode(dbChannel.getCode());
            o.setStatus(dbChannel.getStatus());
            o.setConfig(JSON.toJSONString(payClientPublicKeyConfig));
            o.setId(dbChannel.getId()); // ??????????????? ID
        });

        // ??????
        channelService.updateChannel(reqVO);
        // ????????????????????????
        PayChannelDO channel = channelMapper.selectById(reqVO.getId()); // ???????????????
        assertPojoEquals(reqVO, channel, "config");
        assertPojoEquals(payClientPublicKeyConfig, channel.getConfig());
    }

    @Test
    public void testUpdateChannel_notExists() {
        // ????????????
        AlipayPayClientConfig payClientPublicKeyConfig = getPublicKeyConfig();
        PayChannelUpdateReqVO reqVO = randomPojo(PayChannelUpdateReqVO.class, o -> {
            o.setCode(PayChannelEnum.ALIPAY_APP.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setConfig(JSON.toJSONString(payClientPublicKeyConfig));
        });

        // ??????, ???????????????
        assertServiceException(() -> channelService.updateChannel(reqVO), CHANNEL_NOT_EXISTS);
    }

    @Test
    public void testDeleteChannel_success() {
        // mock ??????
        AlipayPayClientConfig payClientConfig = getCertificateConfig();
        PayChannelDO dbChannel = randomPojo(PayChannelDO.class, o -> {
            o.setCode(PayChannelEnum.ALIPAY_APP.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setConfig(payClientConfig);
        });
        channelMapper.insert(dbChannel);// @Sql: ?????????????????????????????????
        // ????????????
        Long id = dbChannel.getId();

        // ??????
        channelService.deleteChannel(id);
        // ????????????????????????
        assertNull(channelMapper.selectById(id));
    }

    @Test
    public void testDeleteChannel_notExists() {
        // ????????????
        Long id = randomLongId();

        // ??????, ???????????????
        assertServiceException(() -> channelService.deleteChannel(id), CHANNEL_NOT_EXISTS);
    }

    @Test // TODO ????????? null ???????????????
    public void testGetChannelPage() {
        // mock ??????
        AlipayPayClientConfig payClientConfig = getCertificateConfig();
        PayChannelDO dbChannel = randomPojo(PayChannelDO.class, o -> { // ???????????????
            o.setCode(PayChannelEnum.ALIPAY_APP.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setRemark("????????????????????????");
            o.setFeeRate(0.03);
            o.setMerchantId(1L);
            o.setAppId(1L);
            o.setConfig(payClientConfig);
            o.setCreateTime(buildTime(2021,11,20));
        });
        channelMapper.insert(dbChannel);
        // ?????????????????????????????????????????????????????????????????????null ?????????????????????
        dbChannel.setConfig(null);
        // ?????? code ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setCode(PayChannelEnum.WX_PUB.getCode());
        }));
        // ?????? status ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setStatus(CommonStatusEnum.DISABLE.getStatus());
        }));
        // ?????? remark ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o ->{
            o.setConfig(payClientConfig);
            o.setRemark("??????????????????");
        }));
        // ?????? feeRate ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setFeeRate(1.23);
        }));
        // ?????? merchantId ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setMerchantId(2L);
        }));
        // ?????? appId ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setAppId(2L);
        }));
        // ?????? createTime ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setCreateTime(buildTime(2021, 10, 20));
        }));
        // ????????????
        PayChannelPageReqVO reqVO = new PayChannelPageReqVO();
        reqVO.setCode(PayChannelEnum.ALIPAY_APP.getCode());
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        reqVO.setRemark("????????????????????????");
        reqVO.setFeeRate(0.03);
        reqVO.setMerchantId(1L);
        reqVO.setAppId(1L);
        reqVO.setConfig(JSON.toJSONString(payClientConfig));
        reqVO.setCreateTime((new LocalDateTime[]{buildTime(2021,11,19),buildTime(2021,11,21)}));

        // ??????
        PageResult<PayChannelDO> pageResult = channelService.getChannelPage(reqVO);
        // ??????
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbChannel, pageResult.getList().get(0), "config");
        assertPojoEquals(payClientConfig, pageResult.getList().get(0).getConfig());

    }

    @Test
    public void testGetChannelList() {
        // mock ??????
        AlipayPayClientConfig payClientConfig = getCertificateConfig();
        PayChannelDO dbChannel = randomPojo(PayChannelDO.class, o -> { // ???????????????
            o.setCode(PayChannelEnum.ALIPAY_APP.getCode());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setRemark("????????????????????????");
            o.setFeeRate(0.03);
            o.setMerchantId(1L);
            o.setAppId(1L);
            o.setConfig(payClientConfig);
            o.setCreateTime(buildTime(2021,11,20));
        });
        channelMapper.insert(dbChannel);
        // ?????????????????????????????????????????????????????????????????????null ?????????????????????
        dbChannel.setConfig(null);
        // ?????? code ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setCode(PayChannelEnum.WX_PUB.getCode());
        }));
        // ?????? status ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setStatus(CommonStatusEnum.DISABLE.getStatus());
        }));
        // ?????? remark ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o ->{
            o.setConfig(payClientConfig);
            o.setRemark("??????????????????");
        }));
        // ?????? feeRate ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setFeeRate(1.23);
        }));
        // ?????? merchantId ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setMerchantId(2L);
        }));
        // ?????? appId ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setAppId(2L);
        }));
        // ?????? createTime ?????????
        channelMapper.insert(cloneIgnoreId(dbChannel, o -> {
            o.setConfig(payClientConfig);
            o.setCreateTime(buildTime(2021, 10, 20));
        }));
        // ????????????
        PayChannelExportReqVO reqVO = new PayChannelExportReqVO();
        reqVO.setCode(PayChannelEnum.ALIPAY_APP.getCode());
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        reqVO.setRemark("????????????????????????");
        reqVO.setFeeRate(0.03);
        reqVO.setMerchantId(1L);
        reqVO.setAppId(1L);
        reqVO.setConfig(JSON.toJSONString(payClientConfig));
        reqVO.setCreateTime((new LocalDateTime[]{buildTime(2021,11,19),buildTime(2021,11,21)}));

        // ??????
        List<PayChannelDO> list = channelService.getChannelList(reqVO);
        // ??????
        assertEquals(1, list.size());
        assertPojoEquals(dbChannel, list.get(0), "config");
        assertPojoEquals(payClientConfig, list.get(0).getConfig());
    }

    public WXPayClientConfig getV2Config() {
        return new WXPayClientConfig()
                .setAppId("APP00001")
                .setMchId("MCH00001")
                .setApiVersion(WXPayClientConfig.API_VERSION_V2)
                .setMchKey("dsa1d5s6a1d6sa16d1sa56d15a61das6")
                .setApiV3Key("")
                .setPrivateCertContent("")
                .setPrivateKeyContent("");
    }

    public WXPayClientConfig getV3Config() {
        return new WXPayClientConfig()
                .setAppId("APP00001")
                .setMchId("MCH00001")
                .setApiVersion(WXPayClientConfig.API_VERSION_V3)
                .setMchKey("")
                .setApiV3Key("sdadasdsadadsa")
                .setPrivateKeyContent("dsa445das415d15asd16ad156as")
                .setPrivateCertContent("dsadasd45asd4s5a");

    }

    public AlipayPayClientConfig getPublicKeyConfig() {
        return new AlipayPayClientConfig()
                .setServerUrl(AlipayPayClientConfig.SERVER_URL_PROD)
                .setAppId("APP00001")
                .setSignType(AlipayPayClientConfig.SIGN_TYPE_DEFAULT)
                .setMode(AlipayPayClientConfig.MODE_PUBLIC_KEY)
                .setPrivateKey("13131321312")
                .setAlipayPublicKey("13321321321")
                .setAppCertContent("")
                .setAlipayPublicCertContent("")
                .setRootCertContent("");
    }

    public AlipayPayClientConfig getCertificateConfig() {
        return new AlipayPayClientConfig()
                .setServerUrl(AlipayPayClientConfig.SERVER_URL_PROD)
                .setAppId("APP00001")
                .setSignType(AlipayPayClientConfig.SIGN_TYPE_DEFAULT)
                .setMode(AlipayPayClientConfig.MODE_CERTIFICATE)
                .setPrivateKey("")
                .setAlipayPublicKey("")
                .setAppCertContent("13321321321sda")
                .setAlipayPublicCertContent("13321321321aqeqw")
                .setRootCertContent("13321321321dsad");
    }

}
