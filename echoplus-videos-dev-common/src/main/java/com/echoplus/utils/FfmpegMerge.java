package com.echoplus.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * ffmpeg测试类
 *
 * @author Liupeng
 * @createTime 2018-11-13 22:38
 **/
public class FfmpegMerge {

    private String  ffmpegExe;



    public static void main(String[] args) throws IOException {
        FfmpegMerge ffmpegUtils = new FfmpegMerge("D:\\Wx\\ffempg\\bin\\ffmpeg.exe");
        ffmpegUtils.convertor("D:\\Wx\\ffempg\\bin\\test.mp4","D:\\Wx\\echoplus-video-dev\\bgm\\music.mp3",8,"D:\\Wx\\ffempg\\bin\\merge.mp4");
    }

    public void convertor(String sorce,String mp3Source, double seconds, String target) throws IOException {
        //ffmpeg.exe -i test.mp4 -i bgm.mp3 -t 7 -y new.mp4
        List<String> commands = new ArrayList<>();
        commands.add(ffmpegExe);
        commands.add("-i");
        commands.add(sorce);
        commands.add("-i");
        commands.add(mp3Source);
        commands.add("-t");
        commands.add(String.valueOf(seconds));
        commands.add("-y");
        commands.add(String.valueOf(target));

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

    public FfmpegMerge(String ffmpegExe) {
        this.ffmpegExe = ffmpegExe;
    }
}