/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.pleisto.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import com.pleisto.Operator;
import com.pleisto.OperatorInfo;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.pleisto.SandboxResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OperatorInfoTest {
    @TempDir
    private static Path tempDir;

    @Test
    public void ok() {
        assertThat(true).isTrue();
    }

  @Test
  public void eval2() throws ExecutionException, InterruptedException {
    final Map<String, String> conf = new HashMap<>();
    conf.put("root", "/opendal/");
    try (final Operator op = Operator.of("memory", conf)) {
      final OperatorInfo info = op.info;
      SandboxResult result = op.evalPythonCode("foobar").get();
      assertThat(false).isTrue();
    } catch (Exception e) {
      assertThat(true).isTrue();
    }
  }

    @Test
    public void eval() throws ExecutionException, InterruptedException {
      final Map<String, String> conf = new HashMap<>();
      conf.put("root", "/opendal/");
      String code = "print('hello world')";
      try (final Operator op = Operator.of("memory", conf)) {
        final OperatorInfo info = op.info;
        SandboxResult result = op.evalPythonCode(code).get();
        assertThat(result).isNotNull();
        System.out.println(result);
        assertThat(result.stdout).isEqualTo("hello world\n");
      }
    }

    @Test
    public void testOperatorInfo() {
        final Map<String, String> conf = new HashMap<>();
        conf.put("root", "/opendal/");
        try (final Operator op = Operator.of("memory", conf)) {
            final OperatorInfo info = op.info;
            assertThat(info).isNotNull();
            assertThat(info.scheme).isEqualTo("memory");

            assertThat(info.fullCapability).isNotNull();
            assertThat(info.fullCapability.read).isTrue();
            assertThat(info.fullCapability.write).isTrue();
            assertThat(info.fullCapability.delete).isTrue();
            assertThat(info.fullCapability.writeCanAppend).isFalse();
            assertThat(info.fullCapability.writeMultiAlignSize).isEqualTo(-1);
            assertThat(info.fullCapability.writeMultiMaxSize).isEqualTo(-1);
            assertThat(info.fullCapability.writeMultiMinSize).isEqualTo(-1);
            assertThat(info.fullCapability.batchMaxOperations).isEqualTo(-1);

            assertThat(info.nativeCapability).isNotNull();
        }
    }
}
