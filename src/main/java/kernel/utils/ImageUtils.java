package kernel.utils;

import javafx.stage.FileChooser;
import kernel.Main;

import java.io.*;

public class ImageUtils {
    public static void save(File image) {
        /*
         *==================================================
         *                 !!important!!
         *        这里的路径一定要写成下面这个样子的路径
         *        the directory must be written as the following
         *==================================================
         */
//        File imageFileCopy = new File("src/kernel/statics/images/", image.getName());
        File imageFileCopy = new File("src/main/resources/kernel/statics/images/", image.getName());

        //创建复制流
        InputStream in = null;
        OutputStream out = null;

        //复制文件
        try {
            if (!imageFileCopy.exists()) {
                imageFileCopy.createNewFile();
            }
            in = new FileInputStream(image);
            out = new FileOutputStream(imageFileCopy);
            byte[] temp = new byte[1024];
            int length = 0;
            while ((length = in.read(temp)) != -1) {
                out.write(temp, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭文件输入输出流
                // close the file io stream
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static File choose(Main mainApp) {
        FileChooser fileChooser = new FileChooser();
        //设置过滤的文件类型
        //setting the file extension to filter
        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter(
                "JPG files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter jpegFilter = new FileChooser.ExtensionFilter(
                "JPEG files (*.jpeg)", "*.jpeg");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter(
                "PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(jpgFilter);
        fileChooser.getExtensionFilters().add(jpegFilter);
        fileChooser.getExtensionFilters().add(pngFilter);
        File image = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        return image;
    }

    public static void delete(String imageName) {
//        File waitToDeletaImage = new File("src/kernel/statics/images/", imageName);
        File waitToDeletaImage = new File("src/main/resources/kernel/statics/images/", imageName);
        waitToDeletaImage.delete();
        System.out.println("cover" + imageName + "deleted");
    }
}
