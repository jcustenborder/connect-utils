/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

class SourceRecordDequeImpl extends ConcurrentLinkedDeque<SourceRecord> implements SourceRecordDeque {
  private static final Logger log = LoggerFactory.getLogger(SourceRecordDequeImpl.class);
  private final Time time;
  private final int maximumCapacity;
  private final int batchSize;
  private final int emptyWaitMs;
  private final int maximumCapacityWaitMs;
  private final int maximumCapacityTimeoutMs;
  private final RateLimiter writeRateLimit;


  SourceRecordDequeImpl(Time time,
                        int maximumCapacity,
                        int batchSize,
                        int emptyWaitMs, int maximumCapacityWaitMs, int maximumCapacityTimeoutMs, RateLimiter writeRateLimit) {
    super();
    this.time = time;
    this.batchSize = batchSize;
    this.maximumCapacity = maximumCapacity;
    this.emptyWaitMs = emptyWaitMs;
    this.maximumCapacityWaitMs = maximumCapacityWaitMs;
    this.maximumCapacityTimeoutMs = maximumCapacityTimeoutMs;
    this.writeRateLimit = writeRateLimit;
  }

  private void waitForCapacity() {
    waitForCapacity(1);
  }

  private void waitForCapacity(int size) {
    if (null != writeRateLimit) {
      this.writeRateLimit.acquire(size);
    }
    if (size() >= this.maximumCapacity) {
      final long start = this.time.milliseconds();
      long elapsed = 0;
      while (size() >= this.maximumCapacity) {
        if (elapsed > this.maximumCapacityTimeoutMs) {
          throw new TimeoutException(
              String.format(
                  "Timeout of %s ms exceeded while waiting for Deque to be drained below %s",
                  this.maximumCapacityTimeoutMs,
                  this.maximumCapacity
              )
          );
        }
        this.time.sleep(this.maximumCapacityWaitMs);
        elapsed = (this.time.milliseconds() - start);
      }
    }
  }

  @Override
  public boolean add(SourceRecord sourceRecord) {
    waitForCapacity();
    return super.add(sourceRecord);
  }

  @Override
  public boolean addAll(Collection<? extends SourceRecord> c) {
    waitForCapacity(c.size());
    return super.addAll(c);
  }

  @Override
  public void addFirst(SourceRecord sourceRecord) {
    waitForCapacity();
    super.addFirst(sourceRecord);
  }

  @Override
  public void addLast(SourceRecord sourceRecord) {
    waitForCapacity();
    super.addLast(sourceRecord);
  }

  @Override
  public List<SourceRecord> newList() {
    return new ArrayList<>(this.batchSize);
  }

  @Override
  public List<SourceRecord> drain() {
    List<SourceRecord> result = newList();
    drain(result);
    return result;
  }

  @Override
  public List<SourceRecord> getBatch() {
    return getBatch(this.emptyWaitMs);
  }

  @Override
  public List<SourceRecord> getBatch(int emptyWaitMs) {
    Preconditions.checkArgument(emptyWaitMs >= 0, "emptyWaitMs should be greater than or equal to 0.");
    final List<SourceRecord> result = newList();

    int count = 0;
    SourceRecord record;
    log.trace("drain() - Attempting to draining {} record(s).", this.batchSize);
    while (count <= this.batchSize && null != (record = this.poll())) {
      result.add(record);
      count++;
    }

    if (count == 0) {
      if (emptyWaitMs > 0) {
        log.trace("drain() - Found no records, sleeping {} ms.", emptyWaitMs);
        this.time.sleep(emptyWaitMs);
      } else {
        log.trace("drain() - Found no records.", emptyWaitMs);
      }
      return null;
    }

    return result;
  }

  @Override
  public boolean drain(List<SourceRecord> records) {
    return drain(records, this.emptyWaitMs);
  }

  @Override
  public boolean drain(List<SourceRecord> records, int emptyWaitMs) {
    Preconditions.checkNotNull(records, "records cannot be null");
    Preconditions.checkArgument(emptyWaitMs >= 0, "emptyWaitMs should be greater than or equal to 0.");

    int count = 0;
    SourceRecord record;
    log.trace("drain() - Attempting to draining {} record(s).", this.batchSize);
    while (count <= this.batchSize && null != (record = this.poll())) {
      records.add(record);
      count++;
    }

    if (count == 0 && emptyWaitMs > 0) {
      log.trace("drain() - Found no records, sleeping {} ms.", emptyWaitMs);
      this.time.sleep(emptyWaitMs);
    }

    return count > 0;
  }
}
