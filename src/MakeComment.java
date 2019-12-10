import org.json.simple.JSONArray;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MakeComment {
    private File Folder;
    private JSONArray arrChartList;

    public MakeComment(Component parentComponent) {
        Folder = new File("comments");

        if (!Folder.exists()) {
            try {
                Folder.mkdir();
                System.out.println("Making Folder is complete");
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else
            System.out.println("Folder is already exist");

        makeRandomCommentTxt(parentComponent);


    }//MakeComment | Constructor

    private void makeRandomCommentTxt(Component parentComponent) {
        String singer, title, albumName;
        int Orig = AppManager.getS_instance().getSite_M_B_G();
        for(int j = 1; j <= 3; j++) {
            AppManager.getS_instance().setSite_M_B_G(j);
            AppManager.getS_instance().DataPassing(parentComponent);
            for (int k = 1; k <= 100; k++) {
                singer = AppManager.getS_instance().getParser().getArtistName(k);
                title = AppManager.getS_instance().getParser().getTitle(k);
                albumName = AppManager.getS_instance().getParser().getAlbumName(k);

                File file = new File("comments\\" + reDefineTitle(title) + ".txt");
                if (!file.exists()) {
                    try {
                        FileWriter fw = new FileWriter(file, true);
                        int rndNum1 = (int) (Math.random() * 10) + 1;
                        int rndNum2;
                        // rndNum1 = How many make comment
                        // rndNum2 = What comment

                        for (int i = 0; i < rndNum1; i++) {
                            rndNum2 = (int) (Math.random() * 10) + 1;
                            switch (rndNum2 % 10) {
                                case 1: {
                                    fw.write(singer + "�� ���� �ϰ� ������\r");
                                    fw.write(Integer.toString((int) (Math.random() * 10000)) + "\r");
                                    break;
                                }//case 1
                                case 2: {
                                    fw.write("�̹� " + title + " �ʹ� ���� �� ���ƿ�\r");
                                    fw.write(Integer.toString((int) (Math.random() * 10000)) + "\r");
                                    break;
                                }//case 2
                                case 3: {
                                    fw.write("�̹� �ٹ� " + albumName + " �ʹ� ���ƿ�\r");
                                    fw.write(Integer.toString((int) (Math.random() * 10000)) + "\r");
                                    break;
                                }
                                case 4: {
                                    fw.write( "�ٹ� �߸��ϸ� ��ٷȽ��ϴ�!\r");
                                    fw.write(Integer.toString((int) (Math.random() * 10000)) + "\r");
                                    break;
                                }
                                case 5: {
                                    fw.write("5252~" + singer + " ��ٷȴٱ�\r");
                                    fw.write(Integer.toString((int) (Math.random() * 10000)) + "\r");
                                    break;
                                }
                                case 6: {
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
                }// file is not exist
            }//for(k)
        }//for(j)
        AppManager.getS_instance().setSite_M_B_G(Orig);
    }//makeRandomCommentTxt method


    private String reDefineTitle(String title){
        String result;
        if(title == null)
            return null;
        result = title.replace("\'","");
        if(result.indexOf("(") != -1)
            result = result.substring(0, title.indexOf("("));
        result = result.replace(" ","");
        result = result.replace("\'","");
        return result;
    }
}//MakeComment
