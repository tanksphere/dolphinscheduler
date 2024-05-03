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

import {defineComponent, getCurrentInstance, toRefs} from 'vue'
import type {Router} from 'vue-router'
import {useRouter} from 'vue-router'
import {
    NButton,
    NDynamicInput,
    NForm,
    NFormItem,
    NInput,
    NInputNumber,
    NRadio,
    NRadioGroup,
    NSelect,
    NSpace,
    NTabPane,
    NTabs
} from 'naive-ui'
import Card from '@/components/card'
import {useI18n} from 'vue-i18n'
import {useCreate} from '@/views/resource/connection/create/use-create'
import {useForm} from '@/views/resource/connection/create/use-form'
import JsonEditorVue from 'json-editor-vue3'

import styles from '../index.module.scss'

export default defineComponent({
    name: 'ResourceConnectionCreate',
    setup() {
        const router: Router = useRouter()

        const {state} = useForm()

        const {
            handleTestConnection,
            handleSaveConnection,
            rawOptions,
            handleUrlInput,
            handleMethodChange,
            handleContentTypeChange,
            handleParamsUpdate,
            handleHttpBody,
        } = useCreate(state)

        const trim = getCurrentInstance()?.appContext.config.globalProperties.trim

        return {
            rawOptions,
            handleTestConnection,
            handleSaveConnection,
            handleUrlInput,
            handleParamsUpdate,
            handleMethodChange,
            handleContentTypeChange,
            handleHttpBody,
            ...toRefs(state),
            trim
        }
    },
    render() {
        const {t} = useI18n()
        return (
            <Card title={t('resource.connection.create_connection')}>
                <NForm
                    rules={this.rules}
                    label-placement="left"
                    label-align="left"
                    ref='fileFormRef'
                    label-width="110"
                    require-mark-placement="right-hanging"
                    class={styles['form-content']}
                >
                    <NFormItem label={t('resource.connection.connection_name')} path='name'>
                        <NInput
                            clearable
                            allowInput={this.trim}
                            v-model={[this.fileForm.name, 'value']}
                            placeholder={t('resource.connection.enter_name_tips')}
                            style={{width: '500px'}}
                            class='input-file-name'
                        />
                        {/*<div style={{flex:1}}/>*/}
                        <NButton
                            type='info'
                            size='large'
                            loading={this.saveLoading}
                            style={{width: '90px', marginLeft: '20px'}}
                            onClick={this.handleSaveConnection}
                        >
                            {t('resource.connection.save')}
                        </NButton>
                    </NFormItem>
                    <NFormItem label={t('resource.connection.connection_url')} path='url'>
                        <NInput
                            clearable
                            allowInput={this.trim}
                            onInput={this.handleUrlInput}
                            v-model={[this.fileForm.url, 'value']}
                            placeholder={t('resource.connection.enter_url_tips')}
                            style={{width: '500px'}}
                            class='input-file-name'
                        />
                        <NButton
                            color='#04BE35'
                            size='large'
                            loading={this.testLoading}
                            style={{width: '90px', marginLeft: '20px'}}
                            onClick={this.handleTestConnection}
                        >
                            {t('resource.connection.test')}
                        </NButton>
                    </NFormItem>
                    <NSpace>
                        <NFormItem label={t('resource.connection.http_method')} path='httpMethod'>
                            <NRadioGroup v-model:value={this.fileForm.httpMethod}
                                         onChange={this.handleMethodChange}
                                         name="httpMethod"
                            >
                                <NSpace>
                                    <NRadio value={'GET'}>{'GET'}</NRadio>
                                    <NRadio value={'POST'}>{'POST'}</NRadio>
                                    {/*<NRadio value={'PUT'}>{'PUT'}</NRadio>*/}
                                    {/*<NRadio value={'PATCH'}>{'PATCH'}</NRadio>*/}
                                    {/*<NRadio value={'DELETE'}>{'DELETE'}</NRadio>*/}
                                    {/*<NRadio value={'HEAD'}>{'HEAD'}</NRadio>*/}
                                    {/*<NRadio value={'OPTIONS'}>{'OPTIONS'}</NRadio>*/}
                                </NSpace>
                            </NRadioGroup>
                        </NFormItem>
                        <NFormItem
                            style={{marginLeft: '100px', width:'250px'}}
                            label={t('resource.connection.time_out')} path='timeout'>
                            <NInputNumber v-model:value={this.fileForm.timeout} clearable
                                          // show-button={false}
                                          min={0}
                                          v-slots={{
                                              suffix: () => t('resource.connection.second')
                                          }}
                            >
                            </NInputNumber>
                        </NFormItem>
                    </NSpace>
                    <NFormItem label={t('resource.connection.http_header')} path='httpHeader'>
                        <NDynamicInput
                            v-model:value={this.fileForm.httpHeader}
                            preset="pair"
                            key-placeholder="Key"
                            value-placeholder="Value"
                            style={{width: '600px'}}
                        />
                    </NFormItem>
                    {/*<NFormItem label={t('resource.connection.http_header')} path='httpHeader'>*/}
                    {/*    <pre>{ JSON.stringify(this.fileForm.httpHeader, null, 2) }</pre>*/}
                    {/*</NFormItem>*/}
                    <NFormItem label={t('resource.connection.http_params')} path='httpHeader'>
                        <NDynamicInput
                            v-model:value={this.fileForm.httpParams}
                            onUpdateValue={this.handleParamsUpdate}
                            preset="pair"
                            key-placeholder="Key"
                            value-placeholder="Value"
                            style={{width: '600px'}}
                        >
                            {/*    <template default={ value }>*/}
                            {/*    <div style="display: flex; align-items: center; width: 100%">*/}
                            {/*        <NCheckbox*/}
                            {/*            v-model:checked={value.isCheck}*/}
                            {/*            style="margin-right: 12px"*/}
                            {/*        />*/}
                            {/*        <n-input-number*/}
                            {/*            v-model:value="value.num"*/}
                            {/*            style="margin-right: 12px; width: 160px"*/}
                            {/*        />*/}
                            {/*        <n-input v-model:value="value.string" type="text" />*/}
                            {/*    </div>*/}
                            {/*</template>*/}
                        </NDynamicInput>
                    </NFormItem>

                    <NFormItem v-show={this.fileForm?.httpMethod === 'POST'}
                               label={t('resource.connection.http_content_type')} path='httpContentType'>
                        <NRadioGroup v-model:value={this.fileForm.httpContentType}
                                     onChange={this.handleContentTypeChange}
                                     name="httpContentType">
                            <NSpace>
                                <NRadio value={'NONE'}>{'none'}</NRadio>
                                <NRadio value={'FORM-DATA'}>{'form-data'}</NRadio>
                                <NRadio value={'X-WWW-FORM-URLENCODED'}>{'x-www-form-urlencoded'}</NRadio>
                                <NRadio value={'RAW'}>{'raw'}</NRadio>
                                {/*<NRadio value={'binary'}>{'binary'}</NRadio>*/}
                            </NSpace>
                        </NRadioGroup>
                        <NSelect
                            v-show={this.fileForm?.httpMethod === 'POST' && this.fileForm?.httpContentType === 'RAW'}
                            defaultValue={[this.fileForm.requestDataFormat]}
                            v-model={[this.fileForm.requestDataFormat, 'value']}
                            options={this.rawOptions}
                            style={{width: '150px', marginLeft: '10px'}}
                            class='select-file-format'
                        />
                    </NFormItem>
                    <NFormItem
                        v-show={this.fileForm?.httpMethod === 'POST' && (this.fileForm?.httpContentType === 'FORM-DATA' || this.fileForm?.httpContentType === 'X-WWW-FORM-URLENCODED')}
                        label={t('resource.connection.http_form_params')} path='formParams'>
                        <NDynamicInput
                            v-model:value={this.fileForm.formParams}
                            preset="pair"
                            key-placeholder="Key"
                            value-placeholder="Value"
                            style={{width: '600px'}}
                        />
                    </NFormItem>
                    {/*<NFormItem v-show={this.fileForm?.httpMethod === 'POST' && this.fileForm?.httpContentType === 'RAW'}*/}
                    {/*           label={t('resource.connection.request_data_format')} path='requestDataFormat'>*/}
                    {/*    <NRadioGroup v-model:value={this.fileForm.requestDataFormat} name="requestDataFormat">*/}
                    {/*        <NSpace>*/}
                    {/*            <NRadio value={'text'}>{'text'}</NRadio>*/}
                    {/*            <NRadio value={'Javascript'}>{'javascript'}</NRadio>*/}
                    {/*            <NRadio value={'JSON'}>{'json'}</NRadio>*/}
                    {/*            <NRadio value={'html'}>{'html'}</NRadio>*/}
                    {/*            <NRadio value={'xml'}>{'xml'}</NRadio>*/}
                    {/*        </NSpace>*/}
                    {/*    </NRadioGroup>*/}
                    {/*</NFormItem>*/}
                    <NFormItem v-show={this.fileForm?.httpMethod === 'POST' && this.fileForm?.httpContentType === 'RAW'}
                               label={t('resource.connection.http_raw_body')} path='httpBody'>
                        <JsonEditorVue
                            language="zh-CN"
                            class={[styles['json-editor']]}
                            v-model={this.fileForm.httpBody}
                            onChange={this.handleHttpBody}
                        ></JsonEditorVue>

                    </NFormItem>
                </NForm>
                <NTabs type='line' animated>
                    <NTabPane name='Body' tab={t('resource.connection.response_body')}>
                        <JsonEditorVue
                            language="zh-CN"
                            class={[styles['json-editor-response']]}
                            v-model={this.httpBodyResponse}
                        ></JsonEditorVue>
                    </NTabPane>
                    <NTabPane name='Headers' tab={t('resource.connection.response_headers')}>
                        <pre>{JSON.stringify(this.httpResponseHeaders)}</pre>
                    </NTabPane>
                    <NTabPane name='Cookie' tab={t('resource.connection.response_cookie')}>
                        <pre>{JSON.stringify(this.httpResponseCookie)}</pre>
                    </NTabPane>
                </NTabs>
            </Card>


        )
    },
})