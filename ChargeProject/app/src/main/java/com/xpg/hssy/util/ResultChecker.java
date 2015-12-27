package com.xpg.hssy.util;

import org.json.JSONObject;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * ResultChecker
 *
 * @author Jason 2015 10 8
 * @version 1.0
 * @category 支付宝公钥验签
 */
public class ResultChecker {
    public static final int RESULT_INVALID_PARAM = 0;

    public static final int RESULT_CHECK_SIGN_FAILED = 1;

    public static final int RESULT_CHECK_SIGN_SUCCEED = 2;

    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    private String publicKey;

    String mContent;

    public ResultChecker(String content, String publicKey) {

        this.mContent = content;
        this.publicKey = publicKey;

    }

    public int checkSign() {

        int retVal = RESULT_CHECK_SIGN_SUCCEED;

        try {
            JSONObject objContent = string2JSON(this.mContent, ";");

            String result = objContent.getString("result");

            result = result.substring(1, result.length() - 1);
            // 获取待签名数据

            int iSignContentEnd = result.indexOf("&sign_type=");

            String signContent = result.substring(0, iSignContentEnd);
            // 获取签名

            JSONObject objResult = string2JSON(result, "&");

            String signType = objResult.getString("sign_type");
            signType = signType.replace("\"", "");

            String sign = objResult.getString("sign");
            sign = sign.replace("\"", "");

            // 进行验签 返回验签结果

            if (signType.equalsIgnoreCase("RSA")) {

                if (!doCheck(signContent, sign, publicKey))

                    retVal = RESULT_CHECK_SIGN_FAILED;

            }else{
                retVal = RESULT_CHECK_SIGN_FAILED;
            }

        } catch (Exception e) {

            retVal = RESULT_INVALID_PARAM;

            e.printStackTrace();

        }

        return retVal;

    }

    private JSONObject string2JSON(String str, String split) {

        JSONObject json = new JSONObject();

        try {

            String[] arrStr = str.split(split);

            for (int i = 0; i < arrStr.length; i++) {

                String[] arrKeyValue = arrStr[i].split("=");

                json.put(arrKeyValue[0], arrStr[i].substring(arrKeyValue[0].length() + 1));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return json;

    }


    public static boolean doCheck(String content, String sign, String publicKey) {

        try {

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            byte[] encodedKey = Base64.decode(publicKey);

            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);

            signature.update(content.getBytes("utf-8"));

            boolean bverify = signature.verify(Base64.decode(sign));

            return bverify;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;

    }

}
