package com.echoplus.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ffmpeg测试类
 *
 * @author Liupeng
 * @createTime 2018-11-13 22:38
 **/
public class FfmpegUtils {

    private String  ffmpegExe;



    public static void main(String[] args) throws IOException {
        // 生成的截图>ffmpeg.exe -ss 00:00:01 -y -i new.mp4 -vframes 1   new.jpg
        FfmpegUtils ffmpegUtils = new FfmpegUtils("D:\\Wx\\ffempg\\bin\\ffmpeg.exe");
        ffmpegUtils.convertor("D:\\Wx\\ffempg\\bin\\test.mp4","D:\\Wx\\ffempg\\bin\\test2.avi");
    }

    public void convertor(String sorce, String target) throws IOException {
        List<String> commands = new ArrayList<>();
        commands.add(ffmpegExe);
        commands.add("-i");
        commands.add(sorce);
        commands.add(target);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);
         Process procss = builder.start();

        InputStream errorStream = procss.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        if (errorStream != null) {
            errorStream.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
    }

    public FfmpegUtils(String ffmpegExe) {
        this.ffmpegExe = ffmpegExe;
    }
}