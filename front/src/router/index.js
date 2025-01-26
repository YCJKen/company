import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/auth'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import OfferListView from '../views/offer/OfferListView.vue'
import OfferCreateView from '../views/offer/OfferCreateView.vue'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/home',
        name: 'home',
        component: HomeView,
        meta: { requiresAuth: true }  // 需要认证
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView
    },
    {
        path: '/register',
        name: 'register',
        component: RegisterView
    },
    {
        path: '/offers',
        name: 'offers',
        component: OfferListView,
        meta: { requiresAuth: true }
    },
    {
        path: '/offers/create',
        name: 'offer-create',
        component: OfferCreateView,
        meta: { requiresAuth: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!getToken()) {
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            })
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router 