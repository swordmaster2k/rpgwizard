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
RUN add-apt-repository ppa:openjdk-r/ppa && apt-get -yq update && apt-get -yq install openjdk-10-jdk

# Wwitch back to default user
USER 1000