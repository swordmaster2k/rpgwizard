# Xubuntu Docker for testing
FROM consol/ubuntu-xfce-vnc
ENV REFRESHED_AT 2018-03-18

# Switch to root user to install additional software
USER 0

# Update package listings
RUN apt-get -yq update

# Install add-apt-repository
RUN apt-get -yq install software-properties-common

# Install a Java
RUN add-apt-repository ppa:openjdk-r/ppa && apt-get -yq update && apt-get -yq install openjdk-11-jdk

# Install npm
RUN apt-get install -yq curl
RUN curl -sL https://deb.nodesource.com/setup_14.x | bash -
RUN apt-get install -yq nodejs
RUN npm install -g jshint

# Switch back to default user
USER 1000