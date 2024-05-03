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

import {h, reactive, ref} from 'vue'
import {useI18n} from 'vue-i18n'
import ButtonLink from '@/components/button-link'
import {NEllipsis} from 'naive-ui'
import {calculateTableWidth, COLUMN_WIDTH_CONFIG,} from '@/common/column-width-config'
import {useConnectionState} from './use-connection'

export function useTable() {
    const {t} = useI18n()

    const variables = reactive({
        columns: [],
        id: ref(),
        name: ref(),
        url: ref(),
        currentRowIndex: ref(-1),
        resourceList: ref(),
        folderShowRef: ref(false),
        uploadShowRef: ref(false),
        isReupload: ref(false),
        renameShowRef: ref(false),
        searchRef: ref(),
        renameInfo: ref({
            name: '',
            description: '',
            fullName: '',
            user_name: ''
        }),
        reuploadInfo: ref({
            name: '',
            description: '',
            fullName: '',
            user_name: ''
        }),
        pagination: ref({
            page: 1,
            pageSize: 10,
            itemCount: 0,
            pageSizes: [10, 30, 50]
        })
    })

    const createColumns = (variables: any) => {
        variables.columns = [
            {
                title: '#',
                key: 'id',
                ...COLUMN_WIDTH_CONFIG['index'],
                render: (_row: any, index: number) => index + 1
                // render: (row: any, index: number) => h(
                //     NEllipsis,
                //     COLUMN_WIDTH_CONFIG['index'],
                //     () => row.id
                // )
            },
            {
                title: t('resource.file.name'),
                key: 'name',
                ...COLUMN_WIDTH_CONFIG['userName'],
                render: (row: any, index: number) => {
                    return h(
                        ButtonLink,
                        {
                            onClick: () => {
                                variables.currentRowIndex = index
                                // console.log('00000')
                                // console.log(variables.resourceList.table[variables.currentRowIndex])
                                // console.log('11111')
                            }
                        },
                        {
                            default: () =>
                                h(
                                    NEllipsis,
                                    COLUMN_WIDTH_CONFIG['userName'],
                                    () => row.name
                                )
                        }
                    )
                }
            }
            // ,
            // {
            //   title: t('resource.file.tenant_name'),
            //   ...COLUMN_WIDTH_CONFIG['userName'],
            //   key: 'user_name'
            // }
        ]
    }

    const setPagination = (count: number) => {
        variables.pagination.itemCount = count
        if(count>0){
            setTimeout(()=>{
                variables.currentRowIndex = 0;
            },100)
        }
    }

    const {getConnectionListState} = useConnectionState(setPagination)

    const requestData = () => {
        variables.resourceList = getConnectionListState(
            variables.searchRef,
            variables.pagination.page,
            variables.pagination.pageSize
        )
    }

    const updateList = () => {
        variables.pagination.page = 1
        requestData()
    }

    return {
        variables,
        tableWidth: calculateTableWidth(variables.columns) || 100,
        requestData,
        updateList,
        createColumns,
    }
}
