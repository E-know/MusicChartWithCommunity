import java.awt.Component;
import java.util.HashMap;

import javax.swing.ProgressMonitor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author SejongUniv ��â��
 * @version 1.7
 *
 **/

public abstract class MusicChartParser {
	
	protected int songCount = 0;	 		// �뷡 ������ ���
	protected String url;			 		// �Ľ��� �� ����Ʈ url
	protected JSONArray chartList; 	 		// ��Ʈ 100� ���� ������ ���� JSONArray, JSONObject 100���� �̷��������
	protected JSONObject songDetailInfo;	// �뷡 �� ���� �� ������ ���� JSONObject, ��Ʈ 100����� ���� �� ���� �����鸸 ������

	// �Ľ� ������ ������ ���ڿ���
	protected String isNotParsed = "�Ľ��� ���������� �̷������ �ʾҽ��ϴ� :(";
	protected String isOnlyChartParse = "�ش� �޼ҵ�� ��Ʈ �Ľ̿��� ��밡���� �޼ҵ� �Դϴ� :(";
	protected String isOnlyDetailParse = "�ش� �޼ҵ�� �뷡 1���� �� ���� �Ľ̿��� ��밡���� �޼ҵ� �Դϴ� :(";
	protected String jsonDontHaveKey = "JSONObject ���� �ش� Ű ���� �����ϴ� :(";
	protected String plzUseRightJSONObject = "�ùٸ� JSONObject ���� ������ּ��� :(";
	protected String songDetailParsingTitle = "�� ���� �Ľ���..";
	protected String songDetailParsingMessage = "�ش� �뷡�� ���� �� ������ �Ľ��ϴ� ���Դϴ� :)";
	
	// ��Ʈ 100���� �Ľ��ϴ� abstract �޼ҵ�, �� ���� ����Ʈ �ļ����� �ʼ��� �ٸ��� �����ؾ� ��
	// parentComponent�� JPanel, JFrame ���� ������ �Ľ��� �ϸ鼭 �ش� Ŭ������ �ε�â�� �����
	// ProgressMonitor�� ��������� �� ���� ����ϸ� ���װ� �־� ProgressMonitor �κи� �ּ�ó�� �صξ���, ���� ���� ������ �Ʒ��� �������
	public abstract void chartDataParsing(Component parentComponent);
	
	// �뷡 �� ���� �� ������ �Ľ��ϴ� abstract �޼ҵ�, ���� parameter���� ������
	// parentComponent�� JPanel, JFrame ���� ������ �Ľ��� �ϸ鼭 �ش� Ŭ������ �ε�â�� �����
	// ProgressMonitor�� ��������� �� ���� ����ϸ� ���װ� �־� ProgressMonitor �κи� �ּ�ó�� �صξ���, ���� ���� ������ �Ʒ��� �������
	public abstract void songDetailDataParsing(String songId, Component parentComponent);
	public abstract void songDetailDataParsing(JSONObject jObj, Component parentComponent);
	public abstract void songDetailDataParsing(int rank, JSONArray chartListData, Component parentComponent);
	public abstract void songDetailDataParsing(String title, JSONArray chartListData, Component parentComponent); // ����õ �ϴ� �޼ҵ�, title�� �´� �����͸� ó������ ã�ư��� �ϱ� ������ �� �� ��ȿ������
	
	protected Thread chartThread;				// ��Ʈ 100���� �Ľ��� �� ����� Thread 
	protected Thread songDetailThread;			// �뷡 �� � ���� �� ������ �Ľ��� �� ����� Thread
	
	/*
	 * @deprecated
	 * �Ľ� �߿� �ε�â���� ����� ProgressMonitor
	 * �ٵ� �̰� ����ϸ� Thread�� ������� �ʴ� ������ ���ϴ� ��� ProgressMonitor�� ������ �ʴ� ���װ� �־� Deprecated ó���س�����
	 * ProgressBar ���� Ŀ�����ؼ� ����ؾ� �� ��, ���� �ļ������� �ּ�ó�� �صξ���
	 */
	@Deprecated
	protected ProgressMonitor progressMonitor;

	public boolean isParsed() { // �Ľ��� �̷�������� �Ǵ��ϴ� �޼ҵ�
		// chartDataParsing()�̳� songDetailDataParsing()�� �� ���̶� ȣ�������� songCount�� 1 �̻���
		if (songCount == 0)
			return false;
		else
			return true;
	} // boolean isParsed()
	
	public JSONArray getChartList() { // ��Ʈ 100� ���� ������ ��� JSONArray�� chartList�� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) {// �Ľ��� �� ���� ������
			System.out.println(isNotParsed);
			return null;
		}

		if (songCount == 1) { // �뷡 �� � ���� �� �Ľ��� �ߴٸ�
			System.out.println("getChartList() : " + isOnlyChartParse);
			return null;
		}

		return chartList;
	} // JSONArray getChartList()
	
	public JSONObject getSongData() { // �뷡 �� � ���� �� ������ ��� JSONObject�� songDetailInfo�� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �� ���� ������
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 100) { // ��Ʈ 100� ���� �Ľ��� �ߴٸ�
			System.out.println("getSongData() : " + isOnlyDetailParse);
			return null;
		}
		
		return songDetailInfo;
	} // JSONObject getSongData()
	
	public JSONObject getSongData(int rank) { // �뷡 �� � ���� �� ���� �Ǵ� ��Ʈ 100���� �뷡 1� ���� ������ ��ȯ�ϴ� �޼ҵ�

		if (rank < 1 || rank > 100) { // 1 <= rank <= 100�� ����� �������
			System.out.println("1 ~ 100�� �̳��� ������ �Է����ּ��� :)");
			return null;
		}
		
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) 		// �뷡 �� � ���� �� ���� �Ľ��� �̷�����ٸ� �� ���� ��ȯ(�� ������ ������ ����� ����)
			return songDetailInfo;
		else						// ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray�� �ִ� rank ������ �´� ���Ҹ� ��ȯ
			return (JSONObject) chartList.get(rank - 1);
	} // JSONObject getSongData(int rank)
	
	public JSONObject getSongData(String title) { // �뷡 �� � ���� �� ���� �Ǵ� ��Ʈ 100���� �뷡 1� ���� ������ ��ȯ�ϴ� �޼ҵ�

		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}

		if (songCount == 1) // �뷡 �� � ���� �� ���� �Ľ��� �̷�����ٸ� �� ���� ��ȯ(�� ������ ������ ����� ����)
			return songDetailInfo;

		for (int i = 0; i < songCount; i++) { // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray�� �ִ� �뷡�� �� title ���� �´� ���Ҹ� ��ȯ
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return (JSONObject) chartList.get(i);
		}
		
		return null; // �ݺ��� ������ ��ã���� ������ ���� �� = null ��ȯ
	} // JSONObject getSongData(String title)
	
	public int getRank(String title) { // �뷡 ������ ���� �ش� �뷡�� ������ ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return -1;
		}

		if (songCount == 1) { // �뷡 �� � ���� �� ���� �Ľ��� �̷�����ٸ� -1 ��ȯ(�� �������� ������ ���� ����)
			System.out.println("getRank(String title) :" + isOnlyChartParse);
			return -1;
		}

		for (int i = 0; i < songCount; i++) { // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray�� �ִ� �뷡�� �� title ���� �´� ������ ������ ��ȯ
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return Integer.parseInt(((JSONObject) chartList.get(i)).get("rank").toString());
		}
		return -1; // �ݺ��� ������ ��ã���� ������ ���� �� = -1 ��ȯ
	} // int getRank(String title)
	
	public int getRank(JSONObject jObj) { // JSONArray�� ���� �� �ϳ��� �̿��Ͽ� �ش� �뷡�� ������ ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return -1;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return -1;
		}
		
		if (jObj.containsKey("rank")) // rank key�� ��ȿ�� �˻�
			return Integer.parseInt(jObj.get("rank").toString());
		else {
			System.out.println(jsonDontHaveKey);
			return -1;
		}
	} // int getRank(JSONObject jObj)
	
	public String getTitle(int rank) { // �뷡 ������ ���� �ش� �뷡�� ������ ��ȯ�ϴ� �޼ҵ�
		if (rank < 1 || rank > 100) { // 1 <= rank <= 100�� ����� �������
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}

		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}

		if (songCount == 1) { // �뷡 �� � ���� �� ���� �Ľ��� �̷�����ٸ� null ��ȯ(�� �������� ������ ���� ����)
			System.out.println("getTitle(int rank) : " + isOnlyChartParse);
			return null;
		}
		else
			return ((JSONObject) chartList.get(rank - 1)).get("title").toString();
	} // String getTitle(int rank)
	
	public String getTitle(JSONObject jObj) { // JSONArray�� ���� �� �ϳ��� �̿��Ͽ� �ش� �뷡�� ������ ��ȯ�ϴ� �޼ҵ�
		
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("title")) // title key�� ��ȿ�� �˻�
			return jObj.get("title").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	} // String getTitle(JSONObject jObj)
	
	public String getArtistName(int rank) { // �뷡 ������ ���� �ش� �뷡�� ���� �̸��� ��ȯ�ϴ� �޼ҵ�
		if (rank < 1 || rank > 100) { // 1 <= rank <= 100�� ����� �������
			System.out.println("1 ~ 100�� �̳��� ������ �Է����ּ���");
			return null;
		}

		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ�
			System.out.println("getArtistName(int rank) : " + isOnlyChartParse);
			return null;
		}
		return ((JSONObject) chartList.get(rank - 1)).get("artist").toString();
	} // String getArtistName(int rank)
	
	public String getArtistName(String title) { // �뷡 ������ ���� �ش� �뷡�� ���� �̸��� ��ȯ�ϴ� �޼ҵ�

		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) { // �뷡 �Ѱ ���� �� �Ľ��� �̷�����ٸ�
			System.out.println("getArtistName(String title) : " + isOnlyChartParse);
			return null;
		}

		for (int i = 0; i < songCount; i++) { // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray�� �ִ� �뷡�� �� title ���� �´� ������ ���� �̸��� ��ȯ
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("artist").toString();
		}
		return null; // �ݺ��� ������ ��ã���� ������ ���� �� = null ��ȯ
	} // String getArtistName(String title)
	
	public String getArtistName(JSONObject jObj) { // JSONArray�� ���� �� �ϳ��� �̿��Ͽ� �ش� �뷡�� ���� �̸��� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("artist")) // artist key�� ��ȿ�� �˻�
			return jObj.get("artist").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	} // String getArtistName(JSONObject jObj)
	
	public String getAlbumName(int rank) { // �뷡 ������ ���� �ش� �뷡�� �ٹ� �̸��� ��ȯ�ϴ� �޼ҵ�
		if (rank < 1 || rank > 100) { // 1 <= rank <= 100�� ����� �������
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ�
			System.out.println("getAlbumName(int rank) : " + isOnlyChartParse);
			return null;
		}

		return ((JSONObject) chartList.get(rank - 1)).get("albumName").toString();
	} // String getArtistName(int rank)
	
	public String getAlbumName(String title) { // �뷡 ������ ���� �ش� �뷡�� �ٹ� �̸��� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ�
			System.out.println("getAlbumName(String title) : " + isOnlyChartParse);
			return null;
		}

		for (int i = 0; i < songCount; i++) { // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray�� �ִ� �뷡�� �� title ���� �´� ������ �ٹ� �̸��� ��ȯ
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("albumName").toString();
		}
		return null;
	} // String getAlbumName(String title)
	
	public String getAlbumName(JSONObject jObj) { // JSONArray�� ���� �� �ϳ��� �̿��Ͽ� �ش� �뷡�� �ٹ� �̸��� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("albumName")) // albumName key�� ��ȿ�� �˻�
			return jObj.get("albumName").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	} // String getAlbumName(JSONObject jObj)
	
	public String getSongId(int rank) { // �뷡 ������ ���� �ش� �뷡�� �ٹ� �̸��� ��ȯ�ϴ� �޼ҵ�
		if (rank < 1 || rank > 100) { // 1 <= rank <= 100�� ����� �������
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}

		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ�
			System.out.println("getSongId(int rank) : " + isOnlyChartParse);
			return null;
		}

		return ((JSONObject) chartList.get(rank - 1)).get("songId").toString();
	} // String getSongId(int rank)
	
	public String getSongId(String title) { // �뷡 ������ ���� �ش� �뷡�� �뷡 ���̵� ��ȯ�ϴ� �޼ҵ�, �뷡 ���̵�� �� ������ url�� ���� �� ��� ��
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) { // �뷡  �� � ���� �� �Ľ��� �̷�����ٸ�
			System.out.println("getSongId(String title) : " + isOnlyChartParse);
			return null;
		}

		for (int i = 0; i < songCount; i++) { // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray�� �ִ� �뷡�� �� title ���� �´� ������ �뷡 ���̵� ��ȯ
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("songId").toString();
		}
		return null;
	} // String getSongId(String title)
	
	public String getSongId(JSONObject jObj) { // JSONArray�� ���� �� �ϳ��� �̿��Ͽ� �ش� �뷡�� �ٹ� �̸��� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("songId")) // songId key�� ��ȿ�� �˻�
			return jObj.get("songId").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	} // String getSongId(JSONObject jObj)
	
	// getLikeNum()�� BugsChartParser�� GenieChartParser������ ��밡���ϹǷ� �߻�Ŭ���������� ���ܵ�
	// getLikeNum(int rank), getLikeNum(String title)�� MelonChartParser������ ��밡���ϹǷ� �߻�Ŭ���������� ���ܵ�
	
	public String getLikeNum(JSONObject jObj) { // JSONArray�� ���� �� �ϳ��� �̿��Ͽ� �ش� �뷡�� ���ƿ� ������ ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("likeNum")) // likeNum key�� ��ȿ�� �˻�
			return jObj.get("likeNum").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	} // String getLikeNum(JSONObject jObj)
	
	// songDetailDataParsing �Ŀ��� ��밡���� �޼ҵ�
	public String getImageUrl() { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ� �� ���� ū �̹��� url�� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ�
			return songDetailInfo.get("imageUrl").toString();
		
		System.out.println("getImageUrl() : " + isOnlyDetailParse);
		return null;
	} // String getImageUrl()
	
	public String getImageUrl(int rank) { // �뷡 ������ ���� �ش� �뷡�� �̹��� url�� ��ȯ�ϴ� �޼ҵ�
		if (rank < 1 || rank > 100) { // 1 <= rank <= 100�� ����� �������
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}
		
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1)
			return songDetailInfo.get("imageUrl").toString(); // �뷡 �� � ���� �� ���� �Ľ��� �̷�����ٸ� �� ���� ū �̹��� url�� ��ȯ(�� ������ ������ ����� ����)
		else
			return ((JSONObject) chartList.get(rank - 1)).get("smallImageUrl").toString(); // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray�� �ִ� �뷡�� �� ������ �´� ������ ���� �̹��� url�� ��ȯ
	} // String getImageUrl(int rank)
	
	public String getImageUrl(String title) { // �뷡 ������ ���� �ش� �뷡�� �̹��� url�� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}		
		if (songCount == 1) // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ�
			return songDetailInfo.get("imageUrl").toString(); // �뷡 �� � ���� �� ���� �Ľ��� �̷�����ٸ� �� ���� ū �̹��� url�� ��ȯ(�� ������ ����� ����� ����)

		for (int i = 0; i < songCount; i++) { // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray�� �ִ� �뷡�� �� title ���� �´� ������ ���� �̹��� url�� ��ȯ
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("smallImageUrl").toString();
		}
		return null;
	} // String getImageUrl(String title)
	
	public String getImageUrl(JSONObject jObj) { // JSONArray�� ���� �� �ϳ��� �̿��Ͽ� �ش� �뷡�� ���ƿ� ������ ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (songCount == 1) { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ� ū �̹��� url ��ȯ
			if (jObj.containsKey("imageUrl"))
				return jObj.get("imageUrl").toString();
		}
		else { // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� ���� �̹��� url ��ȯ
			if (jObj.containsKey("smallImageUrl"))
				return jObj.get("smallImageUrl").toString();
		}
		
		System.out.println(jsonDontHaveKey);
		return null;
	} // String getImageUrl(JSONObject jObj)
	
	// getGenre(), getGenre(JSONObject jObj)�� MelonChartParser�� GenieChartParser������ ��밡���ϹǷ� �߻�Ŭ���������� ���ܵ�
	// getReleaseDate(), getReleaseDate(JSONObject jObj)�� MelonChartParser������ ��밡���ϹǷ� �߻�Ŭ���������� ���ܵ�
} // MusicChartParser class