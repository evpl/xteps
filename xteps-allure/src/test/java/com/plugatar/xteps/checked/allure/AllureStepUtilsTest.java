/*
 * Copyright 2022 Evgenii Plugatar
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
package com.plugatar.xteps.checked.allure;

import com.plugatar.xteps.base.ThrowingConsumer;
import com.plugatar.xteps.base.ThrowingFunction;
import com.plugatar.xteps.base.XtepsException;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.StepResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link AllureStepUtils}.
 */
final class AllureStepUtilsTest {

    @Test
    void stepNameMethodWithString() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult().setName("step name"));

        final String methodResult = AllureStepUtils.stepName("new step name");
        assertThat(methodResult).isEqualTo("new step name");
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        assertThat(stepResultRef.get().getName()).isEqualTo("new step name");
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepNameMethodWithFunctionThrowsExceptionForNullArg() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());

        assertThatCode(() -> AllureStepUtils.stepName((ThrowingFunction<String, String, ?>) null))
            .isInstanceOf(XtepsException.class);
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepNameMethodWithFunction() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult().setName("step name"));

        final String methodResult = AllureStepUtils.stepName(name -> "new " + name);
        assertThat(methodResult).isEqualTo("new step name");
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        assertThat(stepResultRef.get().getName()).isEqualTo("new step name");
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepDescriptionMethodWithString() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult().setDescription("step description"));

        final String methodResult = AllureStepUtils.stepDescription("new step description");
        assertThat(methodResult).isEqualTo("new step description");
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        assertThat(stepResultRef.get().getDescription()).isEqualTo("new step description");
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepDescriptionMethodWithFunctionThrowsExceptionForNullArg() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());

        assertThatCode(() -> AllureStepUtils.stepDescription((ThrowingFunction<String, String, ?>) null))
            .isInstanceOf(XtepsException.class);
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepDescriptionMethodWithFunction() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult().setDescription("step description"));

        final String methodResult = AllureStepUtils.stepDescription(description -> "new " + description);
        assertThat(methodResult).isEqualTo("new step description");
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        assertThat(stepResultRef.get().getDescription()).isEqualTo("new step description");
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepParameterMethodWithNameAndValue() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());
        final Object paramValue = "param value";

        final Object methodResult = AllureStepUtils.stepParameter("param name", paramValue);
        assertThat(methodResult).isSameAs(paramValue);
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        final List<Parameter> parameters = stepResultRef.get().getParameters();
        assertThat(parameters).hasSize(1);
        final Parameter actualParameter = parameters.get(0);
        assertThat(actualParameter.getName()).isEqualTo("param name");
        assertThat(actualParameter.getValue()).isEqualTo("param value");
        assertThat(actualParameter.getExcluded()).isNull();
        assertThat(actualParameter.getMode()).isNull();
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepParameterMethodWithNameAndValueAndExcluded() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());
        final Object paramValue = "param value";

        final Object methodResult = AllureStepUtils.stepParameter("param name", paramValue, true);
        assertThat(methodResult).isSameAs(paramValue);
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        final List<Parameter> parameters = stepResultRef.get().getParameters();
        assertThat(parameters).hasSize(1);
        final Parameter actualParameter = parameters.get(0);
        assertThat(actualParameter.getName()).isEqualTo("param name");
        assertThat(actualParameter.getValue()).isEqualTo("param value");
        assertThat(actualParameter.getExcluded()).isTrue();
        assertThat(actualParameter.getMode()).isNull();
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepParameterMethodWithNameAndValueAndMode() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());
        final Object paramValue = "param value";

        final Object methodResult = AllureStepUtils.stepParameter("param name", paramValue, Parameter.Mode.HIDDEN);
        assertThat(methodResult).isSameAs(paramValue);
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        final List<Parameter> parameters = stepResultRef.get().getParameters();
        assertThat(parameters).hasSize(1);
        final Parameter actualParameter = parameters.get(0);
        assertThat(actualParameter.getName()).isEqualTo("param name");
        assertThat(actualParameter.getValue()).isEqualTo("param value");
        assertThat(actualParameter.getExcluded()).isNull();
        assertThat(actualParameter.getMode()).isEqualTo(Parameter.Mode.HIDDEN);
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepParameterMethodWithNameAndValueAndExcludedAndMode() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());
        final Object paramValue = "param value";

        final Object methodResult =
            AllureStepUtils.stepParameter("param name", (Object) "param value", true, Parameter.Mode.HIDDEN);
        assertThat(methodResult).isSameAs(paramValue);
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        final List<Parameter> parameters = stepResultRef.get().getParameters();
        assertThat(parameters).hasSize(1);
        final Parameter actualParameter = parameters.get(0);
        assertThat(actualParameter.getName()).isEqualTo("param name");
        assertThat(actualParameter.getValue()).isEqualTo("param value");
        assertThat(actualParameter.getExcluded()).isTrue();
        assertThat(actualParameter.getMode()).isEqualTo(Parameter.Mode.HIDDEN);
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepParameterMethodWithConsumerThrowsExceptionForNullArg() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());

        assertThatCode(() -> AllureStepUtils.stepParameter((ThrowingConsumer<Parameter, ?>) null))
            .isInstanceOf(XtepsException.class);
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void stepParameterMethodWithConsumer() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());

        AllureStepUtils.stepParameter(param ->
            param.setName("param name").setValue("param value").setExcluded(true).setMode(Parameter.Mode.HIDDEN)
        );
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        final List<Parameter> parameters = stepResultRef.get().getParameters();
        assertThat(parameters).hasSize(1);
        final Parameter actualParameter = parameters.get(0);
        assertThat(actualParameter.getName()).isEqualTo("param name");
        assertThat(actualParameter.getValue()).isEqualTo("param value");
        assertThat(actualParameter.getExcluded()).isTrue();
        assertThat(actualParameter.getMode()).isEqualTo(Parameter.Mode.HIDDEN);
        allureLifecycle.stopStep(stepUUID);
    }

    @Test
    void updateStepMethod() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String stepUUID = UUID.randomUUID().toString();
        allureLifecycle.startStep(stepUUID, new StepResult());
        final String stepName = "step name";
        final String stepDescription = "step description";

        AllureStepUtils.updateStep(stepResult -> stepResult.setName(stepName).setDescription(stepDescription));
        final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
        allureLifecycle.updateStep(stepUUID, stepResultRef::set);
        assertThat(stepResultRef.get().getName()).isEqualTo(stepName);
        assertThat(stepResultRef.get().getDescription()).isEqualTo(stepDescription);
        allureLifecycle.stopStep(stepUUID);
    }
}
