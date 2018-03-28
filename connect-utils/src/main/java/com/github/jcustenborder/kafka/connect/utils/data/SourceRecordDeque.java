/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.data;

import org.apache.kafka.connect.source.SourceRecord;

import java.util.Deque;
import java.util.List;

public interface SourceRecordDeque extends Deque<SourceRecord> {
  /**
   * Method returns a new list that has an initial capacity of the batch size the deque is configured
   * for.
   * @return New list with an initial capacity at the batch size.
   */
  List<SourceRecord> newList();

  /**
   * Method will create a new list based on the batch size and drain records to it.
   * @return
   */
  List<SourceRecord> drain();

  /**
   * Method is used drain the records from the queue to the supplied list. newList() should be called
   * to create a list that has the same initial capacity of the batch size.
   * @param records List to append the records to.
   * @return true if records were drained. false if not.
   *
   */
  boolean drain(List<SourceRecord> records);

  /**
   * Method is used drain the records from the queue to the supplied list. newList() should be called
   * to create a list that has the same initial capacity of the batch size.
   * @param records List to append the records to.
   * @param emptyWaitMs Time in milliseconds to wait if no records were drained.
   * @return true if records were drained. false if not.
   */
  boolean drain(List<SourceRecord> records, int emptyWaitMs);
}
