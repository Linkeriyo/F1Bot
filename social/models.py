from django.db import models
from django.contrib.auth.models import User
from string import ascii_uppercase, ascii_lowercase, digits
from random import choice

def generate_token(user, size=70, chars=ascii_uppercase + ascii_lowercase + digits):
    return str(user.id) + "_" + ''.join(choice(chars) for x in range(size))


class UserToken(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    token = models.CharField(max_length=80)
    created_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.user.username