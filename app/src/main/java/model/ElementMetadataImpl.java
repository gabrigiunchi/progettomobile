package model;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import model.interfaces.Element;
import model.interfaces.ElementMetadata;
import utility.ElementFactory;

import static model.interfaces.ElementMetadata.ElementState.ADDED;
import static model.interfaces.ElementMetadata.ElementState.MODIFIED;


/**
 * @author Gabriele Giunchi
 *
 * Implementazione di ElementMetadata
 */
public class ElementMetadataImpl implements ElementMetadata {

	public static final Creator<ElementMetadata> CREATOR = new Creator<ElementMetadata>() {
		@Override
		public ElementMetadata createFromParcel(Parcel source) {
			return new ElementMetadataImpl(source);
		}

		@Override
		public ElementMetadata[] newArray(int size) {
			return new ElementMetadata[size];
		}
	};

	private static final String JSON_ID = "id";
	private static final String JSON_ELEMENT = "element";
	private static final String JSON_LAST_MODIFIED = "last_modified";
	private static final String JSON_STATE = "state";

	private Element element;
	private long lastModified;
	private int id;
	private ElementState state;
	
	public ElementMetadataImpl(JSONObject json) throws JSONException {
		this.lastModified = Long.parseLong(json.getString(JSON_LAST_MODIFIED));
		this.id = Integer.parseInt(json.getString(JSON_ID));
		this.state = ElementState.valueOf(json.getString(JSON_STATE));
		this.element = ElementFactory.create(json.getJSONObject(JSON_ELEMENT));
	}

	public ElementMetadataImpl(ElementMetadata sync) {
		this.element = sync.getElement();
		this.lastModified = sync.getLastModified();
		this.id = sync.getID();
		this.state = sync.getState();
	}
	
	public ElementMetadataImpl(Element element) {
		this.element = element;
		this.lastModified = System.currentTimeMillis();
		this.state = ADDED;
		this.id = 0;
	}

	private ElementMetadataImpl(final Parcel parcel) {
		this.id = parcel.readInt();
		this.state = ElementState.valueOf(parcel.readString());
		this.lastModified = parcel.readLong();
		this.element = ElementFactory.create(parcel);
	}
	
	@Override
	public Element getElement() {
		return this.element;
	}

	@Override
	public long getLastModified() {
		return this.lastModified;
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public ElementState getState() {
		return this.state;
	}

	@Override
	public void setLastModified(final long lastoModified) {
		this.lastModified = lastoModified;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public void setState(ElementState status) {
		this.state = status;

		if(this.state == MODIFIED) {
			this.lastModified = System.currentTimeMillis();
		}
	}

	@Override
	public void modifyElement(Element element) {
		this.element = element;
		this.lastModified = System.currentTimeMillis();
		this.state = MODIFIED;
	}

	@Override
	public JSONObject generateJSON() throws JSONException {
		final JSONObject o = new JSONObject();
		o.put(JSON_LAST_MODIFIED, Long.toString(this.lastModified))
				.put(JSON_STATE, this.state.name())
				.put(JSON_ID, Integer.toString(this.id))
				.put(JSON_ELEMENT, this.element.generateJSON());

		return o;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ElementMetadataImpl other = (ElementMetadataImpl) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SYNC ELEMENT: Id->" + this.id + " , Stato->" + this.state + " ,Id elemento->" + this.element.getID();
	}

	@Override
	public int compareTo(ElementMetadata another) {
		if(another == null) {
			return 1;
		}
		return this.getID() - another.getID();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.state.name());
		dest.writeLong(this.lastModified);
		dest.writeString(this.element.getCategory().name());
		this.element.writeToParcel(dest, flags);
	}
}
