<template>
    <div id="app" :class="background ? background : ''">
        <OrderMessage style="position: fixed; z-index: 1000"></OrderMessage>
        <router-view/>
    </div>
</template>

<script>
    import OrderMessage from "./components/app/OrderMessage"

    export default {
        name: 'App',
        components: {OrderMessage},
        data() {
            return {
                background: "bg-sky-blue"
            }
        },
        created() {
            // 加载上次选择的背景
            let background = window.localStorage.getItem("background");
            if (background) {
                console.log("上传选择的背景为: " + background)
                this.background = background;
            }
        }
    }
</script>

<style>
    * {
        margin: 0;
        padding: 0;
    }

    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
        margin: 0 auto;
        height: 100vh;
    }

    .bg-green {
        background-image: url('assets/background/green.jpeg')
    }

    .bg-sky-blue {
        background-image: url('assets/background/sky-blue.jpeg')
    }

    .bg-light-blue {
        background-image: url('assets/background/light-blue.jpeg')
    }

    .bg-orange {
        background-image: url('assets/background/orange.jpeg')
    }

    .bg-white {
        background-image: url('assets/background/white.png')
    }

    a {
        text-decoration: none;
        color: #000;
    }
</style>
