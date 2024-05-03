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

import type {Router} from 'vue-router'
import {useRouter} from 'vue-router'
import {defineComponent, getCurrentInstance, onMounted, onUnmounted, ref, toRefs, watch} from 'vue'
import {NBreadcrumb, NBreadcrumbItem, NButton, NButtonGroup, NDataTable, NIcon, NPagination, NSpace} from 'naive-ui'
import {useI18n} from 'vue-i18n'
import {SearchOutlined} from '@vicons/antd'
import {useTable} from './use-table'
import Card from '@/components/card'
import styles from '@/views/resource/components/resource/index.module.scss'
import Search from '@/components/input-search'
import ResourceConnectionEdit from './edit'

export default defineComponent({
    name: 'ResourceConnection',
    setup(props) {
        const router: Router = useRouter()
        const breadListRef = ref<Array<string>>()

        const {
            variables,
            tableWidth,
            requestData,
            updateList,
            createColumns,
        } = useTable()


        const handleUpdatePage = (page: number) => {
            variables.pagination.page = page
            requestData()
        }

        const handleUpdatePageSize = (pageSize: number) => {
            variables.pagination.page = 1
            variables.pagination.pageSize = pageSize
            requestData()
        }

        const handleConditions = () => {
            requestData()
        }

        const handleCreateFolder = () => {
            variables.folderShowRef = true
        }

        onUnmounted(() => {
        })
        onMounted(() => {
            createColumns(variables)
            requestData()
        })

        const trim = getCurrentInstance()?.appContext.config.globalProperties.trim

        const handleCreateConnection = () => {
            const name = 'resource-connection-create'
            router.push({
                name,
            })
        }

        watch(useI18n().locale, () => {
            createColumns(variables)
        })

        return {
            breadListRef,
            tableWidth,
            updateList,
            handleConditions,
            handleCreateFolder,
            handleCreateConnection,
            handleUpdatePage,
            handleUpdatePageSize,
            trim,
            ...toRefs(variables)
        }
    },
    render() {
        const {t} = useI18n()

        const {
            handleConditions,
            handleCreateConnection,
            tableWidth
        } = this
        const manageTitle = t('resource.connection.connection_manage')

        return (
            <NSpace vertical>
                <Card>
                    <NSpace justify='space-between'>
                        <NButtonGroup size='small'>
                            <NButton onClick={handleCreateConnection} class='btn-create-file'>
                                {t('resource.connection.create_connection')}
                            </NButton>
                        </NButtonGroup>
                        <NSpace>
                            <Search
                                placeholder={t('resource.file.enter_keyword_tips')}
                                v-model:value={this.searchRef}
                                onSearch={handleConditions}
                            />
                            <NButton size='small' type='primary' onClick={handleConditions}>
                                <NIcon>
                                    <SearchOutlined/>
                                </NIcon>
                            </NButton>
                        </NSpace>
                    </NSpace>
                </Card>
                <Card title={manageTitle}>
                    {{
                        header: () => (
                            <NBreadcrumb separator='>'>
                                {this.breadListRef?.map((item, index) => (
                                    <NBreadcrumbItem>
                                        <NButton
                                            text
                                            disabled={
                                                index > 0 && index === this.breadListRef!.length - 1
                                            }
                                            onClick={() => this.handleBread(index)}
                                        >
                                            {index === 0 ? manageTitle : item}
                                        </NButton>
                                    </NBreadcrumbItem>
                                ))}
                            </NBreadcrumb>
                        ),
                        default: () => (
                            <div style="display:flex">
                                <div style="width:300px">
                                    <NSpace vertical>
                                        <NDataTable
                                            remote
                                            columns={this.columns}
                                            data={this.resourceList?.table}
                                            striped
                                            size={'small'}
                                            class={styles['table-box']}
                                            row-class-name='items'
                                            scrollX={tableWidth}
                                        />
                                        <NSpace justify='center'>
                                            <NPagination
                                                v-model:page={this.pagination.page}
                                                v-model:pageSize={this.pagination.pageSize}
                                                pageSizes={this.pagination.pageSizes}
                                                item-count={this.pagination.itemCount}
                                                onUpdatePage={this.handleUpdatePage}
                                                onUpdatePageSize={this.handleUpdatePageSize}
                                                show-quick-jumper
                                                show-size-picker
                                            />
                                        </NSpace>
                                    </NSpace>
                                </div>
                                <div style="flex:1;min-height:600px">
                                    {
                                        this.currentRowIndex >= 0 ?
                                            <ResourceConnectionEdit
                                                item={this.resourceList?.table[this.currentRowIndex]}/> :
                                            <div>{'暂无数据'}</div>
                                    }
                                </div>
                            </div>
                        )
                    }}
                </Card>

            </NSpace>
        )
    }
})