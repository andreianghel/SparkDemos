package full.app;

import java.io.Serializable;

public class MyRowEntry implements Serializable {
	private static final long serialVersionUID = -5495891074572936784L;

	private Integer id;
	private String value;

	public MyRowEntry() {
	}

	public MyRowEntry(Integer id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "MyRowEntry [id=" + id + ", value=" + value + "]";
	}
}
