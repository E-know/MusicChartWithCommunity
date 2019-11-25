import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MakeComment {
    private File Folder;
    private JSONArray arrChartList;
//    private MelonChartParser melon;


    public MakeComment(){
        Folder = new File("comments");

        if(!Folder.exists()){
            try{
                Folder.mkdir();
                System.out.println("Making Folder is complete");
            }catch(Exception e) {
                e.getStackTrace();
            }
        }else
            System.out.println("Folder is already exist");

        //if
        /*
        arrChartList = melon.getChartList();
        for(int i = 0 ; i < 100; i++){// 100 is SongCount

        }
        */

        makeRandomCommentTxt();


    }//MakeComment | Constructor

    private void makeRandomCommentTxt(/*JSONObject musicInfo*/){
        String singer,title,albumName,releaseDate;
        /*
        singer = musicInfo.get("singer").toString();
        title = musicInfo.get("title").toString();
        albumName = musicInfo.get("albumName").toString();
        releaseDate = musicInfo.get("realeaseDate").toString();
        */
        singer = "IU";
        title = "�� ���� �׷���";
        albumName = "�� �Ա��� ��";
        releaseDate = "32";
        File file = new File( "comments\\" + title + ".txt");
        try{
            FileWriter fw = new FileWriter(file,true);
            int rndNum1 = (int)(Math.random() * 10) + 1;
            int rndNum2;
            boolean[] isWrite = new boolean[]{false};
            // rndNum1 = How many make comment
            // rndNum2 = What comment

            for(int i = 0 ; i< rndNum1 ; i++){
                rndNum2 = (int)(Math.random() * 10) + 1;
                switch(rndNum2 % 10){
                    case 1:{
                        fw.write(singer + "�� ���� �ϰ� ������\r");
                        fw.write(Integer.toString((int)(Math.random() * 10000)) + "\r");
                        break;
                    }//case 1
                    case 2:{
                        fw.write("�̹� " + title + " �ʹ� ���� �� ���ƿ�\r" );
                        fw.write(Integer.toString((int)(Math.random() * 10000)) + "\r");
                        break;
                    }//case 2
                    case 3:{
                        fw.write("�̹� �ٹ� " + albumName + " �ʹ� ���ƿ�\r");
                        fw.write(Integer.toString((int)(Math.random() * 10000)) + "\r");
                        break;
                    }
                    case 4:{
                        fw.write(releaseDate + "�ϸ� ��ٷȽ��ϴ�\r");
                        fw.write(Integer.toString((int)(Math.random() * 10000)) + "\r");
                        break;
                    }
                    case 5:{
                        fw.write("5252~" + singer + " ��ٷȴٱ�\r");
                        fw.write(Integer.toString((int)(Math.random() * 10000)) + "\r");
                        break;
                    }
                    case 6:{
                        fw.write("'" + albumName + "'�ٹ� ���ϵ� �뷡 �� ���� �� ���ƿ�.\r");
                        fw.write(Integer.toString((int) (Math.random() * 10000)) + "\r");
                        break;
                    }
                }//switch
            }//for
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//makeRandomCommentTxt

}//MakeComment

