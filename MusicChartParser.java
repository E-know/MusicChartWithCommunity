import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * @author SejongUniv 18011569 ��â��
 * @version 1.0
 *
 **/

public abstract class MusicChartParser {
	
	protected int songCount = 0;
	protected String url;
	protected JSONArray chartList;
	protected JSONObject songDetailInfo;

	protected String isNotParsed = "�Ľ��� ���������� �̷������ �ʾҽ��ϴ� :(";
	protected String isOnlyChartParse = "�ش� �޼ҵ�� ��Ʈ �Ľ̿��� ��밡���� �޼ҵ� �Դϴ� :(";
	protected String isOnlyDetailParse = "�ش� �޼ҵ�� �뷡 1���� �� ���� �Ľ̿��� ��밡���� �޼ҵ� �Դϴ� :(";
	protected String jsonDontHaveKey = "JSONObject ���� �ش� Ű ���� �����ϴ� :(";
	protected String plzUseRightJSONObject = "�ùٸ� JSONObject ���� ������ּ��� :(";
	
	public abstract void chartDataParsing();
	public abstract void songDetailDataParsing(String songId);
	public abstract void songDetailDataParsing(JSONObject jObj);
	public abstract void songDetailDataParsing(int rank, JSONArray chartListData);
	public abstract void songDetailDataParsing(String title, JSONArray chartListData); // ����õ �ϴ� �޼ҵ� �Դϴ�. title�� �´� �����͸� ó������ ã�ư��� �ϱ� ������ �� �� ��ȿ�����Դϴ�.
	
	public boolean isParsed() {
		if (songCount == 0)
			return false;
		else
			return true;
	}
	
	public JSONArray getChartList() {
		if (!isParsed())
			System.out.println(isNotParsed);

		if (songCount == 1) {
			System.out.println("getChartList() : " + isOnlyChartParse);
			return null;
		}

		return chartList;
	}
	
	public JSONObject getSongData() {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 100) {
			System.out.println("getSongData() : " + isOnlyDetailParse);
			return null;
		}
		
		return songDetailInfo;
	}
	
	public JSONObject getSongData(int rank) {

		if (rank < 1 || rank > 100) {
			System.out.println("1 ~ 100�� �̳��� ������ �Է����ּ��� :)");
			return null;
		}
		
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) // �뷡 �� ������ ����� ��� ���� �ҷ� �� �� ����
			return songDetailInfo;
		else
			return (JSONObject) chartList.get(rank - 1);
	}
	public JSONObject getSongData(String title) {

		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}

		if (songCount == 1) // �뷡 �� ������ ����� ��� ���� �ҷ� �� �� ����
			return songDetailInfo;

		for (int i = 0; i < songCount; i++) {
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return (JSONObject) chartList.get(i);
		}
		// �ݺ��� ������ ��ã���� ������ ���� �� - null ��ȯ
		return null;
	}
	
	public int getRank(String title) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return -1;
		}

		if (songCount == 1) {
			System.out.println("getRank(String title) :" + isOnlyChartParse);
			return -1;
		}

		for (int i = 0; i < songCount; i++) {
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return Integer.parseInt(((JSONObject) chartList.get(i)).get("rank").toString());
		}
		// �ݺ��� ������ ��ã���� ������ ���� �� - -1 ��ȯ
		return -1;
	}	
	
	public int getRank(JSONObject jObj) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return -1;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return -1;
		}
		
		if (jObj.containsKey("rank"))		
			return Integer.parseInt(jObj.get("rank").toString());
		else {
			System.out.println(jsonDontHaveKey);
			return -1;
		}
	}
	// �ٹ���, ���ƿ�, ������, �߸���, �帣�� getRank�� �ϸ� ��ĥ �� �ֱ⿡ ������ ����
	
	public String getTitle(int rank) {
		if (rank < 1 || rank > 100) {
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}

		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}

		if (songCount == 1) {
			System.out.println("getTitle(int rank) : " + isOnlyChartParse);
			return null;
		}
		else
			return ((JSONObject) chartList.get(rank - 1)).get("title").toString();
	}
	
	public String getTitle(JSONObject jObj) {
		
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("title"))
			return jObj.get("title").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	}
	// �ٹ���, ���ƿ�, ������, �߸���, �帣�� getTitle�� �ϸ� ��ĥ �� �ֱ⿡ ������ ����
	
	public String getArtistName(int rank) {
		if (rank < 1 || rank > 100) {
			System.out.println("1 ~ 100�� �̳��� ������ �Է����ּ���");
			return null;
		}

		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) {
			System.out.println("getArtistName(int rank) : " + isOnlyChartParse);
			return null;
		}
		return ((JSONObject) chartList.get(rank - 1)).get("artist").toString();
	}
	
	public String getArtistName(String title) {

		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) {
			System.out.println("getArtistName(String title) : " + isOnlyChartParse);
			return null;
		}

		for (int i = 0; i < songCount; i++) {
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("artist").toString();
		}
		// �ݺ��� ������ ��ã���� ������ ���� �� - null ��ȯ
		return null;
	}
	
	public String getArtistName(JSONObject jObj) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("artist"))		
			return jObj.get("artist").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	}
	
	public String getAlbumName(int rank) {
		if (rank < 1 || rank > 100) {
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) {
			System.out.println("getAlbumName(int rank) : " + isOnlyChartParse);
			return null;
		}

		return ((JSONObject) chartList.get(rank - 1)).get("albumName").toString();
	}
	
	public String getAlbumName(String title) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) {
			System.out.println("getAlbumName(String title) : " + isOnlyChartParse);
			return null;
		}

		for (int i = 0; i < songCount; i++) {
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("albumName").toString();
		}
		return null;
	}
	
	public String getAlbumName(JSONObject jObj) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("albumName"))
			return jObj.get("albumName").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	}
	
	public String getSongId(int rank) {
		if (rank < 1 || rank > 100) {
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}

		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) {
			System.out.println("getSongId(int rank) : " + isOnlyChartParse);
			return null;
		}

		return ((JSONObject) chartList.get(rank - 1)).get("songId").toString();
	}
	
	public String getSongId(String title) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) {
			System.out.println("getSongId(String title) : " + isOnlyChartParse);
			return null;
		}

		for (int i = 0; i < songCount; i++) {
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("songId").toString();
		}
		return null;
	}
	
	public String getSongId(JSONObject jObj) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("songId"))
			return jObj.get("songId").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	}
	
	// getLikeNum()�� BugsChartParser�� GenieChartParser������ ��밡���ϹǷ� �߻�Ŭ���������� ���ܵ�
	// getLikeNum(int rank), getLikeNum(String title)�� MelonChartParser������ ��밡���ϹǷ� �߻�Ŭ���������� ���ܵ�
	
	public String getLikeNum(JSONObject jObj) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (jObj.containsKey("likeNum"))
			return jObj.get("likeNum").toString();
		else {
			System.out.println(jsonDontHaveKey);
			return null;
		}
	}
	
	// songDetailDataParsing �Ŀ��� ��밡���� �޼ҵ�
	public String getImageUrl() {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1)
			return songDetailInfo.get("imageUrl").toString();
		
		System.out.println("getImageUrl() : " + isOnlyDetailParse);
		return null;
	}
	
	public String getImageUrl(int rank) {
		if (rank < 1 || rank > 100) {
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}
		
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1)
			return songDetailInfo.get("imageUrl").toString(); // rank ���� ����� ����
		else
			return ((JSONObject) chartList.get(rank - 1)).get("smallImageUrl").toString();
	}
	
	public String getImageUrl(String title) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}		
		if (songCount == 1)
			return songDetailInfo.get("imageUrl").toString(); // title ���� ����� ����

		for (int i = 0; i < songCount; i++) {
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("smallImageUrl").toString();
		}
		return null;
	}
	
	public String getImageUrl(JSONObject jObj) {
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (songCount == 1) {
			if (jObj.containsKey("imageUrl"))
				return jObj.get("imageUrl").toString();
		}
		else {
			if (jObj.containsKey("smallImageUrl"))
				return jObj.get("smallImageUrl").toString();
		}
		
		System.out.println(jsonDontHaveKey);
		return null;
	}
	
	// getGenre(), getGenre(JSONObject jObj)�� MelonChartParser�� GenieChartParser������ ��밡���ϹǷ� �߻�Ŭ���������� ���ܵ�
	// getReleaseDate(), getReleaseDate(JSONObject jObj)�� MelonChartParser������ ��밡���ϹǷ� �߻�Ŭ���������� ���ܵ�
}
