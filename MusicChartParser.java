import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface MusicChartParser {
	
	public void htmlDataParsing();
	public boolean isParsed();
	
	public JSONArray getChartList();
	public JSONObject getSongData(int rank);
	public JSONObject getSongData(String title);
	
	public int getRank(String title);
	// �ٹ���, ���ƿ�, ������, �߸���, �帣�� getRank�� �ϸ� ��ĥ �� �ֱ⿡ ������ ����
	public String getTitle(int rank);
	// �ٹ���, ���ƿ�, ������, �߸���, �帣�� getTitle�� �ϸ� ��ĥ �� �ֱ⿡ ������ ����
	
	public String getArtistName(int rank);
	public String getArtistName(String title);
	public String getArtistName(JSONObject jObj);
	
	public String getAlbumName(int rank);
	public String getAlbumName(String title);
	public String getAlbumName(JSONObject jObj);
	
	public int getLikeNum(int rank);
	public int getLikeNum(String title);
	public int getLikeNum(JSONObject jObj);
	
	public String getImageUrl(int rank);
	public String getImageUrl(String title);
	public String getImageUrl(JSONObject jObj);
	
	public String getReleaseDate(int rank);
	public String getReleaseDate(String title);
	public String getReleaseDate(JSONObject jObj);
	
	public String getGenre(int rank);
	public String getGenre(String title);
	public String getGenre(JSONObject jObj);
}
