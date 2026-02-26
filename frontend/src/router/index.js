import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import store from '../store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/login/register.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      // 学生端路由
      {
        path: '/student/exams',
        name: 'StudentExams',
        component: () => import('../views/student/exams.vue'),
        meta: { title: '我的考试', icon: 'Document', role: 2 }
      },
      {
        path: '/student/take-exam/:id',
        name: 'TakeExam',
        component: () => import('../views/student/take-exam.vue'),
        meta: { title: '参加考试', hidden: true }
      },
      {
        path: '/student/result/:recordId',
        name: 'ExamResult',
        component: () => import('../views/student/result.vue'),
        meta: { title: '考试结果', hidden: true }
      },
      {
        path: '/student/scores',
        name: 'StudentScores',
        component: () => import('../views/student/scores.vue'),
        meta: { title: '我的成绩', icon: 'Trophy', role: 2 }
      },
      // 教师端路由
      {
        path: '/teacher/banks',
        name: 'QuestionBanks',
        component: () => import('../views/teacher/banks.vue'),
        meta: { title: '题库管理', icon: 'Collection', role: 1 }
      },
      {
        path: '/teacher/questions',
        name: 'Questions',
        component: () => import('../views/teacher/questions.vue'),
        meta: { title: '题目管理', icon: 'EditPen', role: 1 }
      },
      {
        path: '/teacher/exams',
        name: 'TeacherExams',
        component: () => import('../views/teacher/exams.vue'),
        meta: { title: '考试管理', icon: 'Calendar', role: 1 }
      },
      {
        path: '/teacher/exam-detail/:id',
        name: 'ExamDetail',
        component: () => import('../views/teacher/exam-detail.vue'),
        meta: { title: '考试详情', hidden: true }
      },
      {
        path: '/teacher/scores',
        name: 'TeacherScores',
        component: () => import('../views/teacher/scores.vue'),
        meta: { title: '成绩统计', icon: 'DataLine', role: 1 }
      },
      // 管理员路由
      {
        path: '/admin/users',
        name: 'UserManagement',
        component: () => import('../views/admin/users.vue'),
        meta: { title: '用户管理', icon: 'UserFilled', role: 0 }
      },
      {
         path: '/admin/banks',
     name: 'AdminBanks',
  component: () => import('../views/teacher/banks.vue'),
  meta: { title: '题库管理', icon: 'Collection', role: 0 }
},
      // 通用路由
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('../views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User', hidden: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = store.state.token
  const userInfo = store.state.userInfo

  if (to.meta.public) {
    next()
    return
  }

  if (!token) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }

  // 角色权限检查
  if (to.meta.role !== undefined && userInfo) {
    if (to.meta.role !== userInfo.role) {
      ElMessage.error('无权访问')
      next('/dashboard')
      return
    }
  }

  next()
})

export default router
