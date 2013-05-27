package com.cocosw.accessory.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYin {

	public static String getPinYin(final String inputString) {

		final HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

		final char[] input = inputString.trim().toCharArray();
		final StringBuffer output = new StringBuffer();

		try {
			for (final char element : input) {
				if (Character.toString(element).matches("[\\u4E00-\\u9FA5]+")) {
					final String[] temp = PinyinHelper
							.toHanyuPinyinStringArray(element, format);
					if (temp != null && temp.length > 0) {
						output.append(temp[0]);
					}
				} else {
					output.append(Character.toString(element));
				}
			}
		} catch (final BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	/**
	 * 获取汉字串拼音首字母，英文字符不变
	 * 
	 * @param chinese
	 *            汉字串
	 * @return 汉语拼音首字母
	 */
	public static String getFirstSpell(final String chinese) {

		final StringBuffer pybf = new StringBuffer();
		final char[] arr = chinese.toCharArray();
		final HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (final char element : arr) {
			if (element > 128) {
				try {
					final String[] temp = PinyinHelper
							.toHanyuPinyinStringArray(element, defaultFormat);
					if (temp != null) {
						pybf.append(temp[0].charAt(0));
					}
				} catch (final BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(element);
			}
		}
		return pybf.toString().replaceAll("\\W", "").trim().toLowerCase();
	}
}
