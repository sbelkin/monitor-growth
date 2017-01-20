package io.sbelkin.monitorgrowth;

import io.sbelkin.monitorgrowth.impl.DropboxUpload;
import io.sbelkin.monitorgrowth.impl.WebcamCapture;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/*
 * Created by sabelkin on 1/19/2017.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalAccessException("Need an argument with path to configuration file.");
        } else {
            Properties prop = new Properties();
            InputStream input = new FileInputStream(args[0]);
            prop.load(input);
            String accessToken = prop.getProperty("accessToken");
            String failFolder = prop.getProperty("failFolder");
            String uploadFolder = prop.getProperty("uploadFolder");
            Integer minutes = Integer.valueOf(prop.getProperty("minutes"));
            // can null check
            loop(accessToken, failFolder, uploadFolder, minutes);
        }
    }

    public static void loop(String accessToken,String failFolder,String uploadFolder, Integer minutes) throws InterruptedException {
        Integer calculation = minutes * 60 * 1000;
        DropboxUpload dropboxUpload = new DropboxUpload(accessToken, failFolder, uploadFolder);
        while (true) {
            captureLoop(calculation,uploadFolder);
            dropboxUpload.action();
        }
    }

    public static void captureLoop(Integer time,String uploadFolder) throws InterruptedException {
        WebcamCapture webcamCapture = new WebcamCapture(uploadFolder);
        for(int i = 0; i < 20;i++) {
            webcamCapture.action();
            Thread.sleep(time);
        }
    }
}
