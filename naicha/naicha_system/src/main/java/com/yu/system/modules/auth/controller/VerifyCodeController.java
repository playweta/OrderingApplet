package com.yu.system.modules.auth.controller;

import com.yu.common.common.constant.Const;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.service.RedisService;
import com.yu.common.common.util.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Api("验证码")
@RestController
@RequestMapping("/auth")
public class VerifyCodeController {

    @Resource
    private RedisService redisService;

    // 验证码
    @GetMapping("/code")
    public Result<HashMap<String, String>> getVerifyCodeImage(HttpServletResponse response) throws ServiceException, IOException {
        String uuid = UUID.randomUUID().toString();
        BufferedImage image = createVerifyCodeImage(uuid);

        // 转换流信息写出
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        ImageIO.write(image, "JPEG", outputStream);

        return Result.ok(new HashMap<String, String>() {{
            put("uuid", uuid);
            // 手动拼接上 data:image/jpeg;base64, 这是base64图片编码格式!!!
            put("image", "data:image/jpeg;base64," + Base64Utils.encodeToString(outputStream.toByteArray()));
        }});
    }

    private BufferedImage createVerifyCodeImage(String uuid) {
        int width = 90;
        int height = 35;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        Random random = new Random();
        int i1 = random.nextInt(10);
        int i2 = random.nextInt(10);
        int codeResult = i1 + i2;
        log.trace("验证码为: " + codeResult);
        String code = i1 + "+" + i2;
        g.setColor(Color.BLUE);
        g.setFont(new Font("Candara", Font.BOLD, 30));
        g.drawString(code, 17, 25);
        g.dispose();
        redisService.set(Const.CONST_verify_code_redis_prefix + uuid, codeResult, 30);
        return image;
    }
}
