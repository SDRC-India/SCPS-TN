package in.co.sdrc.scpstn.models;


/**
 * 
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
public enum ActionDetailType {

	DATA_SUBMITTED, DATA_EDITED;

	@Override
	public String toString() {
		switch (this) {
		case DATA_SUBMITTED:
			return "DATA SUBMITTED";

		case DATA_EDITED:
			return "DATA EDITED";
		}
		return null;
	}

}
