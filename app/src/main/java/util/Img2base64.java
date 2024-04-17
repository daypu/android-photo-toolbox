package util;

public class Img2base64  {
    public static String img2base64(final String imgurl) {
        final String[] result = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    byte[] imgData = util.FileUtil.readFileByBytes(imgurl);
                    result[0] = util.Base64Util.encode(imgData); // 图片转base64
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join(); // 等待线程执行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }
}
