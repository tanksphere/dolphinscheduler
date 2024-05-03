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
import {onlineCreateConnection, onlineTestConnection} from '@/service/modules/resources'
import {isBoolean, isNumber} from "@vueuse/core";

export function useCreate(state: any) {
    const {t} = useI18n()

    const handleTestConnection = () => {
        // no more pid, as currentDir acts as the pid or parent path right now.
        state.fileFormRef.validate(async (valid: any) => {
            if (!valid) {
                let fileForm = state.fileForm;
                let formHttpParams = [];
                if (fileForm.httpParams != undefined) {
                    let httpParams = [];
                    for (let i = 0, length = fileForm.httpParams.length; i < length; i++) {
                        const keyValueMap = fileForm.httpParams[i]
                        console.log(keyValueMap)
                        if (keyValueMap['key'] == undefined || keyValueMap['key'] == '' || keyValueMap['value'] == undefined || keyValueMap['value'] == '') {
                            console.log('22222')
                            window.$message.error('Params 键值对不可为空')
                            return
                        }
                        let httpParametersType = 'string'
                        if (isNumber(keyValueMap['value'])) {
                            httpParametersType = 'number'
                        } else if (isBoolean(keyValueMap['value'])) {
                            httpParametersType = 'boolean'
                        }
                        httpParams.push({
                            prop: keyValueMap['key'],
                            httpParametersType: httpParametersType,
                            value: keyValueMap['value']
                        })
                        formHttpParams.push(keyValueMap)
                    }
                    fileForm.httpParams = httpParams;
                }
                state.testLoading = true;
                let res = await onlineTestConnection({
                    ...fileForm,
                })
                console.log(res)

                state.fileForm.httpParams = formHttpParams;
                state.testLoading = false;
                state.httpBodyResponse = res.response;
                state.httpResponseHeaders = res.responseHeaders;
                state.httpResponseCookie = res.responseCookie;
                window.$message.success(t('resource.file.success'))
                // router.go(-1)
            }
        })
    }

    const handleSaveConnection = () => {
        // no more pid, as currentDir acts as the pid or parent path right now.
        state.fileFormRef.validate(async (valid: any) => {
            if (!valid) {
                let fileForm = state.fileForm;
                let formHttpParams = [];
                if (fileForm.httpParams != undefined) {
                    let httpParams = [];
                    fileForm.httpParams.forEach((keyValueMap) => {
                        if (keyValueMap['key'] == undefined || keyValueMap['key'] == '' || keyValueMap['value'] == undefined || keyValueMap['value'] == '') {
                            window.$message.error('Params 键值对不可为空')
                            throw new Error()
                        }
                        let httpParametersType = 'string'
                        if (isNumber(keyValueMap.value)) {
                            httpParametersType = 'number'
                        } else if (isBoolean(keyValueMap.value)) {
                            httpParametersType = 'boolean'
                        }
                        httpParams.push({
                            prop: keyValueMap.key,
                            httpParametersType: httpParametersType,
                            value: keyValueMap.value
                        })
                        formHttpParams.push(keyValueMap)
                    })
                    fileForm.httpParams = httpParams;
                }
                state.saveLoading = true;
                await onlineCreateConnection({
                    ...fileForm,
                })
                state.fileForm.httpParams = formHttpParams;
                state.saveLoading = false;
                window.$message.success(t('resource.file.success'))
                // router.go(-1)
            }
        })
    }
    const rawOptions = [
        {
            label: "text",
            value: 'text',
        },
        {
            label: 'Javascript',
            value: 'Javascript'
        },
        {
            label: 'JSON',
            value: 'JSON'
        },
        {
            label: 'html',
            value: 'html'
        },
        {
            label: 'xml',
            value: 'xml'
        }
    ]

    const handleUrlInput = (v: string) => {
        // message.info('[Event input]: ' + v)
        console.log(v)
        if (v.length > 0 && v.indexOf('?') > 0) {
            const paramsArray = v.substr(v.indexOf('?') + 1).split('&');
            const params = {};
            console.log(params)
            state.fileForm.httpParams = [];
            paramsArray.forEach(param => {
                const keyValue = param.split('=');
                const key = decodeURIComponent(keyValue[0]);
                const value = decodeURIComponent(keyValue[1]);
                state.fileForm.httpParams.push({
                    key: key,
                    value: value
                })
            });
        }
    }
    const handleMethodChange = (e: Event) => {
        // message.info('[Event input]: ' + v)
        console.log(e)
        var v = (e.target as HTMLInputElement).value;
        console.log(v)
        state.fileForm.httpBody = null;
        state.fileForm.formParams = null;
        state.fileForm.httpContentType = null;
        state.fileForm.requestDataFormat = null;
        if (v == 'JSON') {
            state.fileForm.httpContentType = 'RAW'
            state.fileForm.requestDataFormat = 'JSON';
        }
    }
    const handleContentTypeChange = (e: Event) => {
        // message.info('[Event input]: ' + v)
        console.log(e)
        var v = (e.target as HTMLInputElement).value;
        console.log(v)
        state.fileForm.httpContentType = v;
        state.fileForm.formParams = null;
        state.fileForm.requestDataFormat = null;
        if (v == 'RAW') {
            state.fileForm.requestDataFormat = 'JSON';
        } else if (state.fileForm.httpContentType == 'FORM-DATA' || state.fileForm.httpContentType == 'X-WWW-FORM-URLENCODED') {
            state.fileForm.formParams = [
                {
                    key: '',
                    value: ''
                }
            ];
        }
    }
    const handleParamsUpdate = (v: []) => {
        // message.info('[Event input]: ' + v)
        console.log(v)
        if (v.length > 0) {
            let split = state.fileForm.url.split('?');
            let params = '';
            v.forEach((valueMap, index) => {
                const key = valueMap['key'];
                const value = valueMap['value'];
                if (key != null && key.toString().length > 0 && value != null && value.toString().length > 0) {
                    if (index > 0) {
                        params = params + '&'
                    }
                    params = params + key + '=' + value;
                }
            });
            params.substring(0, params.length - 1)
            state.fileForm.url = split[0]
            if (params.length > 0) {
                state.fileForm.url = split[0] + '?' + params
            }
        }
    }
    const handleHttpBody = (e: Object) => {
        // message.info('[Event input]: ' + v)
        console.log(e)
        // var v = (e.target as HTMLInputElement).value;
        // console.log(v)
        // state.fileForm.formParams = null;
        // state.fileForm.httpContentType = null;
        // state.fileForm.requestDataFormat = null;
        // if (v == 'JSON') {
        //     if (state.fileForm.httpContentType == 'raw') {
        //         state.fileForm.requestDataFormat = 'json';
        //     } else if (state.fileForm.httpContentType == 'form-data' || state.fileForm.httpContentType == 'x-www-form-urlencoded') {
        //         state.fileForm.formParams = [
        //             {
        //                 key: '',
        //                 value: ''
        //             }
        //         ];
        //     }
        // } else {
        //     state.fileForm.formParams = null;
        //     state.fileForm.httpContentType = null;
        //     state.fileForm.requestDataFormat = null;
        // }
    }


    return {
        handleTestConnection,
        handleSaveConnection,
        rawOptions,
        handleUrlInput,
        handleMethodChange,
        handleContentTypeChange,
        handleParamsUpdate,
        handleHttpBody,
    }
}
