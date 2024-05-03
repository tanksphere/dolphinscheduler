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

import {useAsyncState} from '@vueuse/core'
import {queryConnectionListPaging,} from '@/service/modules/resources'
import {IConnectionListState, ISetPagination,} from './types'
import {ConnectionData} from "@/service/modules/resources/types";

export function useConnectionState(
    setPagination: ISetPagination = {} as ISetPagination
) {
    const getConnectionListState: IConnectionListState = (
        searchVal = '',
        pageNo = 1,
        pageSize = 10
    ) => {
        const {state} = useAsyncState(
            queryConnectionListPaging({
                searchVal,
                pageNo,
                pageSize
            }).then((res: Array<ConnectionData>): any => {
                // const { total } = res
                // setPagination(total)
                // const table = res.totalList.map((item) => {
                //   return item
                // })
                var total = 0;
                const table = (res as any).map((item) => {
                    total++;
                    return item
                })
                setPagination(total)

                return { total, table }
                // return { total, table }
                // return {table}
            }),
            { total: 0, table: [] }
            // {table: []}
        )

        return state
    }

    return {getConnectionListState}
}
