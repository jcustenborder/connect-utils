/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.connect.utils.data;

import com.google.common.base.Preconditions;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Deque used to buffer records from systems using listener threads.
 */
public class SourceRecordConcurrentLinkedDeque extends ConcurrentLinkedDeque<SourceRecord> {
  private static final Logger log = LoggerFactory.getLogger(SourceRecordConcurrentLinkedDeque.class);

  private final int batchSize;
  private final int timeout;

  /**
   * Constructor creates a new instance of the SourceRecordConcurrentLinkedDeque
   *
   * @param batchSize The maximum number of records to return per batch.
   * @param timeout   The amount of time to wait if no batch was returned.
   */
  public SourceRecordConcurrentLinkedDeque(int batchSize, int timeout) {
    this.batchSize = batchSize;
    this.timeout = timeout;
  }

  /**
   * Constructor creates a new instance of the SourceRecordConcurrentLinkedDeque with a batchSize of 1024 and timeout of 0.
   */
  public SourceRecordConcurrentLinkedDeque() {
    this(1024, 0);
  }

  /**
   * Method is used to drain the records from the deque in order and add them to the supplied list.
   *
   * @param records list to add the records to.
   * @return true if records were added to the list, false if not.
   * @throws InterruptedException Thrown if the thread is killed while sleeping.
   */
  public boolean drain(List<SourceRecord> records) throws InterruptedException {
    return drain(records, this.timeout);
  }

  /**
   * Method is used to drain the records from the deque in order and add them to the supplied list.
   *
   * @param records list to add the records to.
   * @param timeout amount of time to sleep if no records are added.
   * @return true if records were added to the list, false if not.
   * @throws InterruptedException     Thrown if the thread is killed while sleeping.
   * @throws IllegalArgumentException Thrown if timeout is less than 0.
   */
  public boolean drain(List<SourceRecord> records, int timeout) throws InterruptedException {
    Preconditions.checkNotNull(records, "records cannot be null");
    Preconditions.checkArgument(timeout >= 0, "timeout should be greater than or equal to 0.");

    if (log.isDebugEnabled()) {
      log.debug("determining size for this run. batchSize={}, records.size()={}", this.batchSize, records.size());
    }

    int count = Math.min(this.batchSize, this.size());

    if (log.isDebugEnabled()) {
      log.debug("Draining {} record(s).", count);
    }

    for (int i = 0; i < count; i++) {
      SourceRecord record = this.poll();

      if (null != record) {
        records.add(record);
      } else {
        if (log.isDebugEnabled()) {
          log.debug("Poll returned null. exiting");
          break;
        }
      }
    }

    if (records.isEmpty() && timeout > 0) {
      if (log.isDebugEnabled()) {
        log.debug("Found no records, sleeping {} ms.", timeout);
      }
      Thread.sleep(timeout);
    }

    return !records.isEmpty();
  }
}
