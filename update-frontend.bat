@echo off
echo Updating frontend environment to point to MongoDB backend...

cd /d C:\Users\ratan\it-company-ui\src\environments

echo export const environment = { > environment.ts
echo   production: false, >> environment.ts
echo   apiUrl: 'http://localhost:8081/api/v1' >> environment.ts
echo }; >> environment.ts

echo export const environment = { > environment.prod.ts
echo   production: true, >> environment.prod.ts
echo   apiUrl: 'http://localhost:8081/api/v1' >> environment.prod.ts
echo }; >> environment.prod.ts

echo Frontend environment updated successfully!
pause