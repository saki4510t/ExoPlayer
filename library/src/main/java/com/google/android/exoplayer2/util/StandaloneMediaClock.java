/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.util;

import android.os.SystemClock;

/**
 * A standalone {@link MediaClock}. The clock can be started, stopped and its time can be set and
 * retrieved. When started, this clock is based on {@link SystemClock#elapsedRealtime()}.
 */
public final class StandaloneMediaClock implements MediaClock {

  private boolean started;

  /**
   * The media time when the clock was last set or stopped.
   */
  private long positionUs;

  /**
   * The difference between {@link SystemClock#elapsedRealtime()} and {@link #positionUs}
   * when the clock was last set or started.
   */
  private long deltaUs;

  /**
   * The media time(ms) on last sync.
   */
  private double lastMediaTimeMs;

  /**
   * The {@link SystemClock#elapsedRealtime()} (ms) on last sync.
   */
  private long lastRealTimeMs;
  /**
    * playback speed factor
    */
  private float speedFactor = 1.0f;

  /**
   * Starts the clock. Does nothing if the clock is already started.
   */
  public void start() {
    if (!started) {
      started = true;
      lastRealTimeMs = SystemClock.elapsedRealtime();
      deltaUs = elapsedRealtimeMinus(positionUs);
    }
  }

  /**
   * Stops the clock. Does nothing if the clock is already stopped.
   */
  public void stop() {
    if (started) {
      positionUs = elapsedRealtimeMinus(deltaUs);
      started = false;
    }
  }

  /**
   * @param timeUs The position to set in microseconds.
   */
  public void setPositionUs(long timeUs) {
    this.positionUs = timeUs;
    deltaUs = elapsedRealtimeMinus(timeUs);
  }

  @Override
  public long getPositionUs() {
    return started ? elapsedRealtimeMinus(deltaUs) : positionUs;
  }

  /**
   * return elapsedRealtime - toSubtractUs as micro secs
   * @param toSubtractUs
   * @return [us]
  */
  private long elapsedRealtimeMinus(final long toSubtractUs) {
    updateMediaTime();
    return (long)(lastMediaTimeMs * 1000) - toSubtractUs;
  }

  public float getSpeedFactor() {
    return speedFactor;
  }

  public void setSpeedFactor(final float factor) {
    this.speedFactor = factor;
  }

  private void updateMediaTime() {
    if (!started) return;
    final long realTimeMs = SystemClock.elapsedRealtime();
    lastMediaTimeMs = lastMediaTimeMs + (realTimeMs - lastRealTimeMs) * speedFactor;
    lastRealTimeMs = realTimeMs;
  }
}
