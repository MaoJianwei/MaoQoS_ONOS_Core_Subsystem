package org.onosproject.mao.qos.base.callable;

import org.onosproject.mao.qos.api.impl.MaoQosObj;
import org.onosproject.mao.qos.base.DeviceElement;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

