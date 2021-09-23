# 数据库备份
backPath=$(date '+/home/naicha/sql/naicha_%G-%m-%d.sql')
mysqldump -hmysql.xxl.today -unaicha -pnaicha123-+ naicha > $backPath

# 恢复数据库
# mysql -hmysql.xxl.today -unaicha -pnaicha123-+
# source /home/naicha_api/sql/naicha_2021-09-12.sql
