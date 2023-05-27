case $1 in
    "build")
        echo "Building..."
        
        docker build . -t universi -f Dockerfile.build

        docker run -v $(pwd):/app/ universi

        docker rmi universi --force
        
        echo "Build complete!"
    ;;
    "run")
        echo "Running..."

        docker compose -p "universi-me" up -d --force-recreate --build
;;
esac