/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {useI18n} from 'vue-i18n'
import {reactive, ref, unref} from 'vue'
import type {FormRules} from 'naive-ui'
import {ICreateConnectionDefaultValue} from './../types'


export function useForm() {
    const {t} = useI18n()

    const state = reactive({
        fileFormRef: ref(),
        fileForm: ref(),
        testLoading: false,
        saveLoading: false,
        httpBodyResponse: ref(),
        httpResponseHeaders: ref(),
        httpResponseCookie: ref(),
        rules: {
            name: {
                required: true,
                trigger: ['input', 'blur'],
                validator() {
                    if (state.fileForm.name === '') {
                        return new Error(t('resource.connection.enter_name_tips'))
                    }
                }
            },
            url: {
                required: true,
                trigger: ['input', 'blur'],
                validator() {
                    if (state.fileForm.url === '') {
                        return new Error(t('resource.connection.enter_url_tips'))
                    }
                }
            },
            // httpMethod: {
            //   required: true,
            //   trigger: ['input', 'blur'],
            //   validator() {
            //     if (state.fileForm.httpMethod === '') {
            //       return new Error(t('resource.connection.enter_content_tips'))
            //     }
            //   }
            // }
        } as FormRules
    })

    return {
        state,
    }
}
