from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate
from annoying.functions import get_object_or_None
import json
from django.contrib.auth.models import User
from social.models import UserToken, generate_token
from django.http import JsonResponse

# Create your views here.
@csrf_exempt
def login(request):
    try:
        data = json.loads(request.POST['data'])
        username = data.get('username')
        password = data.get('password')

        user = authenticate(username=username, password=password)

        if user is not None:
            if user.is_active:
                user_token = get_object_or_None(UserToken, user=user)

                if user_token is None:
                    user_token = UserToken.objects.create(
                        user=user,
                        token=generate_token(user)
                    )

                    return JsonResponse({
                        'result': 'ok',
                        'token': user_token.token
                    })

                user_token.token = generate_token(user)
                user_token.save()

                return JsonResponse({
                    'result': 'ok',
                    'token': user_token.token
                })
                
            return JsonResponse({
                'result': 'error',
                'message': 'user_not_active'
            })
        
        return JsonResponse({
            'result': 'error',
            'message': 'user_not_found'
        })
    
    except Exception as e:
        return JsonResponse({
            'result': 'error',
            'message': str(e)
        })


@csrf_exempt
def register(request):
    try:
        data = json.loads(request.POST['data'])
        username = data.get('username')
        email = data.get('email')
        password = data.get('password')

        user = User.objects.create_user(
            username=username,
            email=email,
            password=password,
        )

        if user is not None:
            if user.is_active:
                return login(request)
            
            return JsonResponse({
                'result': 'error',
                'message': 'user_not_active'
            })
        
        return JsonResponse({
            'result': 'error',
            'message': 'user_not_found'
        })

    except Exception as e:
        return JsonResponse({
            'result': 'error',
            'message': str(e)
        })
