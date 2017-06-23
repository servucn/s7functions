/**
 * 
 */
package com.cn.ucasp.net;

import lombok.Getter;
import lombok.Setter;

/**
 * @author rixiang.yu
 *
 */
public enum DataType {

	Input(129), Output(130), Memory(131), DataBlock(132), Timer(29), Counter(28);
	@Setter
	@Getter
	private int Index;

	private DataType(int i) {
		this.Index = i;
	}
}
