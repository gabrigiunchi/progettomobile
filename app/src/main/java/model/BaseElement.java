package model;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;
import model.interfaces.Element;

/**
 *  @author Gabriele Giunchi
 */
public abstract class BaseElement implements Element {

    private final static String JSON_ID = "id";
    private int id;

    protected BaseElement() {
        this.id = -1;
    }

    protected BaseElement(final int id) {
        this.id = id;
    }

    protected BaseElement(final Parcel parcel) {
        this.id = parcel.readInt();
    }

    protected BaseElement(final JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt(JSON_ID);
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void setID(final int id) {
        this.id = id;
    }

    @Override
    public abstract Category getCategory();

    @Override
    public JSONObject generateJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID, this.id);
        return jsonObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseElement that = (BaseElement) o;

        return id == that.id && getCategory().equals(that.getCategory());

    }

    @Override
    public int hashCode() {
        return id;
    }
}
