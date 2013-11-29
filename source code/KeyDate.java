import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.hadoop.io.WritableComparable;

/**
 * This class implements the WritableComparable class.
 * This class is used as the output key class for the language count map reduce job.
 */
public class KeyDate implements WritableComparable<KeyDate>
{
	public String recordKey = new String();
	public String recordDate= new String();

	@Override
	/**
	 * This function overrides the readFields.
	 */
	public void readFields(DataInput in) throws IOException {
		String keyDate = in.readLine();

		StringTokenizer tokenizer = new StringTokenizer(keyDate);
		if(tokenizer.hasMoreTokens())
			recordKey = tokenizer.nextToken();

		if(tokenizer.hasMoreTokens())
			recordDate = tokenizer.nextToken();
	}

	@Override
	/**
	 * This function is used to write the KeyDate data.
	 */
	public void write(DataOutput out) throws IOException {
		out.writeBytes(recordKey);
		out.writeBytes(" ");
		out.writeBytes(recordDate);
	}

	/**
	 * This function converts the KeyDate data to String.
	 */
	public String toString() {
		return String.format("%s %s", recordKey, recordDate);
	}

	/**
	 * This function is used to compare two KeyDate objects.
	 */
	public int compareTo(KeyDate other) {
		String otherDate = other.recordDate;
		String otherKey = other.recordKey;
		SimpleDateFormat formatter;

		formatter = new SimpleDateFormat("yyyyMMddHH");

		int recordKeyCompareValue = -2;

		try
		{
			Date oDate =  formatter.parse(otherDate);
			Date mDate = formatter.parse(this.recordDate);

			recordKeyCompareValue = this.recordKey.compareTo(otherKey);

			if(recordKeyCompareValue  == 0)
				return mDate.compareTo(oDate);

		} catch (ParseException e) 
		{
		}

		return recordKeyCompareValue;
	}

	/**
	 * This function is used to check equality of two KeyDate objects.
	 */
	public boolean equals(Object other) 
	{
		if (!(other instanceof KeyDate)) 
		{
			return false;
		}

		KeyDate newObject = (KeyDate) other;
		return this.recordKey.equals(newObject.recordKey)
		&& this.recordDate == newObject.recordDate;
	}

	/**
	 * This function returns the hashCode of the KeyDate object.
	 */
	public int hashCode() 
	{
		return this.recordDate.hashCode() ^ this.recordKey.hashCode();
	}
}