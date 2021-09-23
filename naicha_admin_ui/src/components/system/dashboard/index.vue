<template>
    <div style="padding: 10px;">
        <div>
            <div style="margin: 30px;">
                <el-button type="" @click="changeTheme('bg-green')">浅绿</el-button>
                <el-button type="" @click="changeTheme('bg-light-blue')">浅蓝</el-button>
                <el-button type="" @click="changeTheme('bg-sky-blue')">天蓝</el-button>
                <el-button type="" @click="changeTheme('bg-orange')">橘黄</el-button>
                <el-button type="" @click="changeTheme('bg-white')">淡白</el-button>
            </div>
        </div>
        <panel-group @handleSetLineChartData="handleSetLineChartData"/>

        <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
            <line-chart :chart-data="lineChartData"/>
        </el-row>

        <el-row :gutter="32">
            <el-col :xs="24" :sm="24" :lg="8">
                <div class="chart-wrapper">
                    <raddar-chart/>
                </div>
            </el-col>
            <el-col :xs="24" :sm="24" :lg="8">
                <div class="chart-wrapper">
                    <pie-chart/>
                </div>
            </el-col>
            <el-col :xs="24" :sm="24" :lg="8">
                <div class="chart-wrapper">
                    <bar-chart/>
                </div>
            </el-col>
        </el-row>

    </div>
</template>

<script>
    // import GithubCorner from '@/components/GithubCorner'
    import PanelGroup from './components/PanelGroup'
    import RaddarChart from './components/RaddarChart'
    import PieChart from './components/PieChart'
    import LineChart from './components/LineChart'
    import BarChart from './components/BarChart'

    const lineChartData = {
        newVisitis: {
            expectedData: [100, 120, 161, 134, 105, 160, 165],
            actualData: [120, 82, 91, 154, 162, 140, 145]
        },
        messages: {
            expectedData: [200, 192, 120, 144, 160, 130, 140],
            actualData: [180, 160, 151, 106, 145, 150, 130]
        },
        purchases: {
            expectedData: [80, 100, 121, 104, 105, 90, 100],
            actualData: [120, 90, 100, 138, 142, 130, 130]
        },
        shoppings: {
            expectedData: [130, 140, 141, 142, 145, 150, 160],
            actualData: [120, 82, 91, 154, 162, 140, 130]
        }
    }

    export default {
        name: 'DashboardAdmin',
        components: {
            // GithubCorner,
            PanelGroup,
            RaddarChart,
            LineChart,
            PieChart,
            BarChart
        },
        data() {
            return {
                percentage: 77,
                lineChartData: lineChartData.newVisitis
            }
        },
        methods: {
            handleSetLineChartData(type) {
                this.lineChartData = lineChartData[type]
            },
            // 切换主题
            changeTheme(background) {
                window.localStorage.setItem("background", background);
                location.reload();
            }
        }
    }
</script>
