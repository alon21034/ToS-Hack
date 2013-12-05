echo "Start ToS"

read user_input

case "$user_input" in
  a) 
    sh click.sh 200 300
    ;;
  b)
    sh click.sh 300 400
    ;;
esac
