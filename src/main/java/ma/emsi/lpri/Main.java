package ma.emsi.lpri;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Welcome to my small program to Upload you files to HDFS");

        Scanner keyboard = new Scanner(System.in);
        System.out.println(" **** Please insert the absolute path to your file: ");
        String path = keyboard.nextLine();
        System.out.println("Your Path is : " + path);

        System.out.println(" **** Please give a name to your file: ");
        String fileName = keyboard.nextLine();

        if(path != null && fileName != null){
            Upload upload
                    = new Upload("http://172.20.43.105:9870/webhdfs/v1/"+fileName+"?op=CREATE","PUT");

            upload.addFormField("Author", "Ahaidous");
            upload.addFilePart("3dFile",new File(path));
            upload.finish();
        }

    }
}