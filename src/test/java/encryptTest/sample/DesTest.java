package encryptTest.sample;

/**
 * Created by nicolas on 25/08/2015.
 */
@Annotation("toto")
public class DesTest {

	public int a = 5;

	public double b = 5.12;

	public char c = 'c';

	public String d = null;

	public boolean e = "" instanceof String;

	public static final Object f = new Object() {
		public static final long uid = 5L;
	};

	public void e() {
		switch ("a") {
		case "b":
			break;
		}
	}

}
