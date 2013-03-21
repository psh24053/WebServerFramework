/**
 * 
 */
package com.shntec.bp.common;

/**
 * @author 1
 *
 */
public interface AbstractProgressListener {

	abstract void updateProcess(long currentSize, long totalSize);
	
}
