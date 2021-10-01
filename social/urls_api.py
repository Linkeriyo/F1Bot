from django.contrib import admin
from django.urls import path
from social import views

urlpatterns = [
    # API
    path('login/', views.login),
    path('register/', views.register),
]